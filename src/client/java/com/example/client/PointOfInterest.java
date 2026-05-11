package com.example.client;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PointOfInterest {
    public final String name;
    public final int x, z;
    private final List<String> rawMessages;
    private List<Component> parsedMessages;
    private final List<Integer> delaysTicks;

    public PointOfInterest(String name, int x, int z, List<String> messages) {
        this.name = name;
        this.x = x;
        this.z = z;
        this.rawMessages = messages;
        this.parsedMessages = null;
        this.delaysTicks = new ArrayList<>();

        for (String msg : messages) {
            int wordCount = msg.trim().isEmpty() ? 0 : msg.trim().split("\\s+").length;
            int dotCount = countChar(msg, '.');
            int commaCount = countChar(msg, ',');
            int delay = 40 + wordCount * 4 + dotCount * 20 + commaCount * 10;
            delaysTicks.add(delay);
        }
    }

    private int countChar(String str, char ch) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ch) count++;
        }
        return count;
    }

    public List<Component> getParsedMessages() {
        if (parsedMessages == null) {
            parsedMessages = new ArrayList<>();
            for (String raw : rawMessages) {
                parsedMessages.add(parseMessage(raw));
            }
        }
        return parsedMessages;
    }

    public int getMessageDelay(int index) {
        return (index >= 0 && index < delaysTicks.size()) ? delaysTicks.get(index) : 60;
    }

    private Component parseMessage(String raw) {
        MutableComponent result = Component.literal("");

        Pattern tokenizer = Pattern.compile(
                "\\[(?:[^\\]]*)\\]\\[(?:https?://[^\\]]+)\\]" +   // [текст][url]
                        "|\\[(?:[^\\]]*)\\]\\((?:[^\\)]+)\\)" +           // [текст](url)
                        "|\\[(?:https?://[^\\]]+)\\]" +                   // [url]
                        "|\\*[^*]+\\*" +                                  // *важное*
                        "|\\b\\d{4}\\b"                                   // год
        );

        Matcher m = tokenizer.matcher(raw);
        int lastEnd = 0;
        while (m.find()) {
            if (m.start() > lastEnd) {
                String plain = raw.substring(lastEnd, m.start());
                result = result.append(Component.literal(plain));
            }

            String token = m.group();
            if (token.startsWith("[")) {
                if (token.contains("][")) {
                    int split = token.indexOf("][");
                    String text = token.substring(1, split);
                    String url = token.substring(split+2, token.length()-1);
                    result = result.append(makeLink(text, url));
                } else if (token.contains("](")) {
                    int split = token.indexOf("](");
                    String text = token.substring(1, split);
                    String url = token.substring(split+2, token.length()-1);
                    result = result.append(makeLink(text, url));
                } else {
                    String url = token.substring(1, token.length()-1);
                    result = result.append(makeLink(url, url));
                }
            } else if (token.startsWith("*") && token.endsWith("*")) {

                String text = token.substring(1, token.length()-1);
                result = result.append(Component.literal(text)
                        .withStyle(style -> style.withColor(ChatFormatting.YELLOW).withBold(true)));
            } else {

                result = result.append(Component.literal(token)
                        .withStyle(style -> style.withColor(ChatFormatting.YELLOW)));
            }
            lastEnd = m.end();
        }
        if (lastEnd < raw.length()) {
            result = result.append(Component.literal(raw.substring(lastEnd)));
        }

        return result;
    }

    private MutableComponent makeLink(String text, String url) {
        MutableComponent link = Component.literal(text)
                .withStyle(style -> style
                        .withColor(ChatFormatting.BLUE)
                        .withUnderlined(true));
        try {
            URI uri = URI.create(url);
            link = link.withStyle(style -> style.withClickEvent(new ClickEvent.OpenUrl(uri)));
        } catch (IllegalArgumentException ignored) {}
        return link;
    }
}
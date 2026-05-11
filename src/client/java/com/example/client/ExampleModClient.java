package com.example.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class ExampleModClient implements ClientModInitializer {

	private static final String TARGET_IP = "mc.recublic.com";
	private static final int RADIUS = 15;
	private static final long COOLDOWN_MS = 5 * 60 * 1000;


	private static final List<PointOfInterest> POINTS = Arrays.asList(
			new PointOfInterest("Гомель", 9500, 8860, Arrays.asList(
					"Добро пожаловать в *Гомельский Государственный Дворцово Парковый ансамбль имени Румянцевых и Паскевичей*. [Открыть изображение][https://i.imgur.com/SxfAes6.png]",
					"Парк был возведен на месте старого детинца города *Гомий*. В будущем землю получит *Пётр Александрович Румянцев* за военные достижения, еще позже построенный дворец выкупит *Иван Фёдорович Паскевич*. Уже при *Паскевичах* парк приобретет великолепный вид.",
					"Каждое дерево парка было построено вручную игроками *zZlag* и *kosciuszko69*",
					"Парк построили: *zZlag*, *ewel*, *Vanya_jirniy*, §4Буслик§r, *kosciuszko69*, *Verior4ik* и множество финансистов"
			)),
			new PointOfInterest("Дворец Румянцевых и Паскевичей", 9639, 8865, Arrays.asList(
					"Главная достопримечательность Гомеля - это *Дворец Румянцевых и Паскевичей*. [Открыть изображение][https://i.imgur.com/rAR04u1.png]",
					"в *1794 году* Семья *Румянцевых* заселились в только возведенный *Дворец*, вид он имел несколько отличимый от нынешнего. Имелось только два этажа северного крыла и 3 Этажа южной башни. [Открыть изображение][https://i.imgur.com/O6lT42d.png]",
					"На третьем поколении *Сергей Румянцев* отдает Дворец в казну. И позже, в *1834* за *800 тысяч рублей* дворец был выкуплен *И.Ф. Паскевичем*.",
					"При *Паскевичах* дворец полностью преобразился и на данный момент его внешний вид почти не отличается от Дворца во времена Паскевичей.",
					"В *Послевоенное время* дворец находился в руинах, но в скором времени реконструирован и до *1980-х годов* являлся домом пионеров, пока те не переселились в *Дворец Юность*",
					"Строители: *zZlag*, *ewel*, §4Буслик§r"
			)),
			new PointOfInterest("Церковный комплекс имени святых Петра и Павла", 9555, 8670, Arrays.asList(
					"Перед вами находится *Собор святых Петра и Павла*. Внутри двора так-же находятся *Церковь Иоанна Предтечи*, *Источники освящённой воды* и церковный магазин. [Открыть изображение][https://i.imgur.com/kG48oAQ.png]",
					"Сам *Петропавловский Собор* был возведен в *1809 году* по просьбе *Николая Румянцева*.",
					"В советское время Собор служил *планетарием*.",
					"Строители: *zZlag*, *ewel*."
			)),
			new PointOfInterest("Усыпальница Паскевичей", 9680, 8670, Arrays.asList(
					"Часовня-усыпальница князей *Паскевичей* - построена в *1889 году* для перезахоронения всего рода Паскевичей. [Открыть изображение][https://i.imgur.com/IWWCEGF.png]",
					"Здание посередине этой площади, является *часовней*, а здание находящееся ближе к собору - *Усыпальница*",
					"Постройка реализована с помощью *палочки отладки* и тысячи кликов по блокам",
					"Строители: *zZlag*"
			)),
			new PointOfInterest("Музей Белорусских традиций", 9680, 8720, Arrays.asList(
					"Филиал *Ветковского музея старообрядчества и белорусских традиций* является местом проведения мероприятий по сохранению *белорусских традиций* [Открыть изображение][https://i.imgur.com/IjHWkt3.png]",
					"Очень важное для сохранения *белорусской культуры* место",
					"Строители: *kosciuszko69*, *Verior4ik*"
			)),
			new PointOfInterest("КРЦ Европа", 9500, 8914, Arrays.asList(
					"Ранее вместо *КРЦ Европа* здесь располагался ресторан \"Беларусь\", который так-же являлся местом тусовок для молодежи в *советское время*. [Открыть изображение][https://i.imgur.com/aT21tsJ.png]",
					"На данный момент внутри здания работает только ресторан и клуб, *Казино закрыли в апреле 2026 года*.",
					"Строители: *zZlag*"
			)),
			new PointOfInterest("Юность", 9455, 8995, Arrays.asList(
					"Нынешний \"Дворец творчества детей и молодежи\" стоит на месте старого детского Кинотеатра, который стал частью *Дворца \"Юность\"* (Так раньше называлось здание) [Открыть изображение][https://i.imgur.com/ftZwntd.png]",
					"В ходе строительства в *1880-х годах* была найдено место захоронения старого *Гомия*, остатки монгольских стрел, не разложившиеся остатки старого деревянного замка, стоявшего в этом месте",
					"После постройки в *1888 году* \"Юность\" стала местом работы *Пионеров*",
					"Строители: *zZlag*"
			)),
			new PointOfInterest("Лебединое Озеро", 9619, 8996, Arrays.asList(
					"Лебединое озеро является устьем пересохшей реки *Гомеюк*. По одной из версий город Гомель или тогдашний *Гомий*, берет свое название от этой реки. [Открыть изображение][https://i.imgur.com/4avvECm.png]",
					"Через Озеро проходят два моста: *Старый мост* и *Стальной мост*(которого на сервере нету, т.к. он проходит над озером на высоте 15 метров за счет того что его начало и конец соединяют две возвышенности, которые не имеются на рельефной карте Сервера)",
					"В озере обитают *лебеди*, черные и белые, которые живут в своих домиках.",
					"Озера так же как и *Дворец* является самой узнаваемой достопримечательностью Гомеля",
					"Строители: *kosciuszko69*, *Verior4ik*"
			)),
			new PointOfInterest("Обзорная башня и Зимний сад", 9620, 9091, Arrays.asList(
					"Обзорная башня и *Зимний сад* объединены одной историей. С 19 Века здания служили одним *сахарным заводом*. [Открыть изображение][https://i.imgur.com/lG3pnHE.png]",
					"Завод закрылся в *1887 году* и с того времени здания имеют ту же функцию что и на нынешний момент. Башня является точкой обозрения всего парка, а *Зимний сад* служит местом сохранения экзотических видов растений",
					"Строители: *kosciuszko69*"
			)),
			new PointOfInterest("Парк аттракционов", 9522, 9091, Arrays.asList(
					"Парк аттракционов находится в тенистом парке(территория парка за лебединым озером), в нем находится множество различных аттракционов, в том числе и *Колесо обозрения*, которая может служить ориентиром для жителей города благодаря своей высоте [Открыть изображение][https://i.imgur.com/D6DSfAZ.png]",
					"Строители: *zZlag*, *kosciuszko69*"
			)),
			new PointOfInterest("Набережная", 9662, 8990, Arrays.asList(
					"Набережная реки *Сож* в Гомеле — живописная пешеходная зона протяженностью *2.5км*, являющаяся частью дворцово-паркового ансамбля. [Открыть изображение][https://i.imgur.com/EBaCXY4.png]",
					"Это главное место отдыха с обустроенными аллеями, скамейками, фонтанами и видами на реку, которое соединяет центральный парк с городским пляжем через популярный *243-метровый пешеходный мост*.",
					"Под основной дорогой набережной, на уровне воды, находятся бетонные плиты, которые во время паводков затапливаются. По этой причине на сервере их нет :) [Открыть изображение][https://i.imgur.com/nUDmynn.png]",
					"Строители: *zZlag*, *kosciuszko69*"
			))
	);


	private boolean onTargetServer = false;
	private PointOfInterest currentPoint = null;
	private boolean waitingForN = false;
	private boolean isShowingSequence = false;
	private int messageIndex = 0;
	private int tickCounter = 0;
	private int currentDelay = 0;
	private List<Component> scheduledMessages = null;
	private int joinDelayTicks = 0;
	private static final int JOIN_DELAY = 40;


	private final Map<PointOfInterest, Long> cooldownMap = new HashMap<>();

	private KeyMapping infoKey;

	@Override
	public void onInitializeClient() {
		infoKey = new KeyMapping("key.examplemod.info", GLFW.GLFW_KEY_N, "key.category.examplemod");
		KeyBindingHelper.registerKeyBinding(infoKey);

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			ServerData serverData = client.getCurrentServer();
			if (serverData != null && serverData.ip != null) {
				onTargetServer = serverData.ip.contains(TARGET_IP);
				if (onTargetServer) {
					resetAllState();
					cooldownMap.clear();


					scheduledMessages = new ArrayList<>();
					scheduledMessages.add(makePrefixedMessage("Привет! Спасибо что доверился и скачал этот мод!"));
					scheduledMessages.add(makePrefixedMessage("Автор мода: ")
							.append(Component.literal("zZlag").withStyle(style -> style.withColor(ChatFormatting.RED))));
					scheduledMessages.add(makePrefixedMessage("Я работал над этим модом 2 месяца и поэтому очень признателен вашей заинтересованности"));
					scheduledMessages.add(makePrefixedMessage("Пройдите к главному входу в Гомельский парк (прямо от остановки). Там вы найдете Карту на которой указаны желтым все гиды, подходя к гиду он вам будет рассказывать о той достопримечательности у которой вы находитесь"));
					scheduledMessages.add(makePrefixedMessage("Приятной Игры!"));
					joinDelayTicks = 0;
				}
			}
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			onTargetServer = false;
			resetAllState();
			cooldownMap.clear();
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {

			if (scheduledMessages != null && !scheduledMessages.isEmpty()) {
				joinDelayTicks++;
				if (joinDelayTicks >= JOIN_DELAY) {
					for (Component msg : scheduledMessages) {
						if (client.player != null) {
							client.player.displayClientMessage(msg, false);
						}
					}
					scheduledMessages = null;
				}
			}
			if (!onTargetServer || client.player == null) return;


					if (!isShowingSequence) {
						PointOfInterest newPoint = findNearbyPoint(client.player.getX(), client.player.getZ());
						if (newPoint != currentPoint) {
							if (currentPoint != null && newPoint == null) {
								resetAllState();
							}
							currentPoint = newPoint;
							if (newPoint != null) {

								Long lastPressTime = cooldownMap.get(newPoint);
								long now = System.currentTimeMillis();
								if (lastPressTime == null || (now - lastPressTime) > COOLDOWN_MS) {

									waitingForN = true;


									client.player.displayClientMessage(
											createGuideMessage(Component.literal(newPoint.name)),
											false
									);


									client.player.displayClientMessage(
											Component.literal("(Нажмите N для подробностей)")
													.withStyle(style -> style.withColor(ChatFormatting.RED).withBold(true)),
											false
									);
								} else {

									waitingForN = false;
								}
							}
						}
					}



			while (infoKey.consumeClick()) {
				if (currentPoint != null && waitingForN && !isShowingSequence) {
					startSequence(currentPoint, client.player);
				}
			}


			if (isShowingSequence && currentPoint != null && client.player != null) {
				tickCounter++;
				if (tickCounter >= currentDelay) {
					List<Component> messages = currentPoint.getParsedMessages();
					if (messageIndex < messages.size()) {
						client.player.displayClientMessage(
								createGuideMessage(messages.get(messageIndex)),
								false
						);
						int justSent = messageIndex;
						messageIndex++;
						if (messageIndex < messages.size()) {
							currentDelay = currentPoint.getMessageDelay(justSent);
							tickCounter = 0;
						} else {
							isShowingSequence = false;
							resetAllState();
						}
					}
				}
			}
		});
	}

	private void startSequence(PointOfInterest point, net.minecraft.world.entity.player.Player player) {
		List<Component> messages = point.getParsedMessages();
		if (messages.isEmpty()) return;


		cooldownMap.put(point, System.currentTimeMillis());

		isShowingSequence = true;
		waitingForN = false;


		player.displayClientMessage(createGuideMessage(messages.get(0)), false);
		messageIndex = 1;
		tickCounter = 0;

		if (messages.size() > 1) {
			currentDelay = point.getMessageDelay(0);
		} else {
			isShowingSequence = false;
			resetAllState();
		}
	}


	private Component createGuideMessage(String text) {
		MutableComponent guideName = Component.literal("⚠Гид⚠")
				.withStyle(style -> style
						.withColor(ChatFormatting.GOLD)
						.withBold(true));
		return Component.literal("<")
				.append(guideName)
				.append(Component.literal("> "))
				.append(Component.literal(text));
	}

	private MutableComponent makePrefixedMessage(String text) {
		return Component.literal("[")
				.withStyle(style -> style.withColor(ChatFormatting.GRAY))
				.append(Component.literal("GomelGID").withStyle(style -> style.withColor(ChatFormatting.WHITE)))
				.append(Component.literal("] ").withStyle(style -> style.withColor(ChatFormatting.GRAY)))
				.append(Component.literal(text).withStyle(style -> style.withColor(ChatFormatting.BLUE)));
	}


	private Component createGuideMessage(Component message) {
		MutableComponent guideName = Component.literal("⚠Гид⚠")
				.withStyle(style -> style
						.withColor(ChatFormatting.GOLD)
						.withBold(true));
		return Component.literal("<")
				.append(guideName)
				.append(Component.literal("> "))
				.append(message);
	}

	private void resetAllState() {
		currentPoint = null;
		waitingForN = false;
		isShowingSequence = false;
		messageIndex = 0;
		tickCounter = 0;
		currentDelay = 0;
	}

	private PointOfInterest findNearbyPoint(double playerX, double playerZ) {
		for (PointOfInterest poi : POINTS) {
			if (getDistance(playerX, playerZ, poi.x, poi.z) <= RADIUS) {
				return poi;
			}
		}
		return null;
	}

	private double getDistance(double x1, double z1, int x2, int z2) {
		double dx = x1 - x2;
		double dz = z1 - z2;
		return Math.sqrt(dx * dx + dz * dz);
	}
}
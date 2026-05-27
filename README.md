# Пастухов Александр Андреевич

**Группа:** ИКС-432

---

# RandomWalk
##  Часть 1. Модель Random Walk

В данной программе реализована **модель случайного блуждания (Random Walk)**.  
Для метода `move()` используется следующая логика изменения координат объекта.

### Формулы движения

x(t + Δt) = x(t) + v * Δt * cos(θ)  
y(t + Δt) = y(t) + v * Δt * sin(θ)

где:

* **v** — скорость,
* **Δt** — шаг по времени,
* **θ** — случайный угол, равномерно распределённый в [0, 2π].

---

###  Первичный конструктор

Объявляется непосредственно после имени класса и используется для инициализации свойств объекта при создании.

```kotlin
class Person(val name: String, var age: Int) {
    init {
        println("Создан объект Person: $name, $age")
    }
}
```

---

###  Вторичный конструктор

Позволяет задать альтернативные варианты инициализации объекта.

```kotlin
constructor(name: String, age: Int, city: String) : this(name, age) {
    println("Дополнительный конструктор: $name, $age, $city")
}
```

---

###  Структура модуля Random Walk

* **Movable.kt** — интерфейс, описывающий объекты, которые могут двигаться (свойства `x`, `y`, `speed`, метод `move()`).
* **Human.kt** — класс `Human`, реализующий `Movable`, двигается случайно.
* **Driver.kt** — наследник `Human`, двигается по оси X.
* **Main.kt** — запускает симуляцию движения с многопоточностью.

---

##  Часть 2. SimpleCalculator (ПР4)

###  Описание

Приложение **Calculator** реализовано в среде Android Studio на языке **Kotlin**.  
Функционал — базовый калькулятор, способный выполнять арифметические операции:
**сложение, вычитание, умножение, деление**, а также корректно обрабатывать выражения.

###  Основная логика

* Получение выражения из `TextView`
* Проверка корректности ввода
* Замена специальных символов (`×`, `÷`) на `*`, `/` с помощью `replace()`
* Расчёт выражения и вывод результата
* Обработка ошибок (например, деление на ноль)

###  Ключевые элементы

* `EditText` и `TextView` для отображения ввода и результата
* `Button` для каждой операции (`+`, `-`, `×`, `÷`, `C`, `=` и цифры 0–9)
* `onClickListener` для обработки нажатий
* `ExpressionEvaluator` или ручной парсер для вычислений

###  Основные файлы

* **MainActivity.kt** — главный экран
* **activity_main.xml** — интерфейс калькулятора
* **AndroidManifest.xml** — описание Activity приложения

---

##  Часть 3. Рефакторинг и разделение по Activity

###  Цель

Реализовать **разделение функционала по Activity** и создание “хаба” для переходов.  
Главная `MainActivity` теперь содержит кнопки перехода к отдельным экранам.

###  Реализация

* **MainActivity.kt** — “меню навигации” (hub), содержит кнопки:

  * `Calculator`
  * `Player`
  * `Location`
  * `Telephony`
  * `Sockets`
  * `Views`

* **CalculatorActivity.kt** — перенесён весь функционал калькулятора

* **AndroidManifest.xml** — обновлён с корректными ссылками на новые Activity

  ```xml
  <activity
            android:name=".CalculatorActivity"
            android:exported="false"
            android:label="@string/title_activity_calculator_activity.kt"
            android:theme="@style/Theme.Calculator" />
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
  ```

###  Внешний вид

Главный экран (`MainActivity`) оформлен в виде сетки кнопок (2 ряда по 3).  
Используется фиолетовая цветовая схема, закруглённые углы (`rounded_button.xml`), и равномерное размещение элементов.  


---

# Часть 4. Реализация MediaPlayerActivity

---

## Цель работы
Научиться работать с внутренней/внешней памятью Android и реализовать работу со звуковыми файлами через класс `MediaPlayer`.

## Задачи

1. Создать отдельное Activity для плеера (`MediaPlayerActivity`).
2. Реализовать:
   - воспроизведение и паузу текущего трека;
   - обработку `onPause()` (автоматическая пауза при сворачивании);
   - регулировку громкости (`SeekBar`);
   - перемотку и отображение длительности трека (`SeekBar`);
   - загрузку и отображение списка треков из памяти устройства;

---

## Основные файлы

|            Файл             |                        Назначение                     |
|-----------------------------|-------------------------------------------------------|
| `MediaPlayerActivity.kt`    | Логика плеера: работа с файлами, MediaPlayer, SeekBar |
| `activity_media_player.xml` | Интерфейс плеера                                      |
| `MainActivity.kt`           | Главный экран с кнопками перехода                     |
| `AndroidManifest.xml`       | Регистрация Activity и разрешений                     |

---

## Возможности приложения

- Список треков с выбором и воспроизведением
- Отображение текущего состояния (“Играет: <имя>”)
- Регулировка громкости
- Перемотка с помощью SeekBar
- Пауза при выходе или сворачивании Activity

---

# Часть 5. Реализация LocationActivity

---

## Цель работы
Получить доступ к данным о местоположении Android-устройства, выводить координаты на экран и сохранять параметры в JSON-файл.

---

## Функциональные задачи

1. Создать отдельное Activity — `LocationActivity`.
2. Получить доступ к геолокации через `LocationManager`.
3. Запрашивать разрешения `ACCESS_FINE_LOCATION`.
4. Получать и выводить параметры:
   - широту (Latitude);
   - долготу (Longitude);
   - высоту (Altitude);
   - текущее время.
5. Обрабатывать обновления GPS через `onLocationChanged()`.
6. Сохранять каждое обновление в файл `location_log.json` во внутренней памяти.

---

## Основные файлы
 
|           Файл           |                            Назначение                           |
|--------------------------|-----------------------------------------------------------------|
| `LocationActivity.kt`    | Логика получения координат и сохранения данных                  |
| `activity_location.xml`  | Интерфейс для отображения координат                             |
| `AndroidManifest.xml`    | Разрешения на геолокацию и регистрация Activity                 |

---

## Как работает LocationActivity

---

### 1. Проверка и запрос разрешений

При запуске Activity выполняется проверка системного разрешения:

```kotlin
checkPermissions()
```

Если разрешение не выдано — вызывается запрос:

```kotlin
requestPermissions()
```

---

### 2. Проверка включённости GPS

Перед запуском геолокации приложение проверяет, включены ли:

- GPS_PROVIDER  
- NETWORK_PROVIDER

Если GPS выключён — открывает настройки устройства:

```kotlin
startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
```

---

### 3. Получение последней известной локации

Если система хранит последнее GPS-значение, оно отображается сразу:

```kotlin
locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
```

---

### 4. Подписка на обновления локации

Приложение получает координаты каждые **1 секунду** и при смещении на **1 метр**:

```kotlin
locationManager.requestLocationUpdates(
    LocationManager.GPS_PROVIDER,
    1000L,
    1f,
    this
)
```

---

### 5. Обновление интерфейса

Метод:

```kotlin
onLocationChanged()
```

обновляет:

- Latitude  
- Longitude  
- Altitude  
- текущее время (формат `HH:mm:ss`)

---

### 6. Сохранение данных в JSON-файл

Каждое обновление записывается во внутреннюю память:

```json
{
  "latitude": …,
  "longitude": …,
  "altitude": …,
  "time": …
}
```

Файл: **`location_log.json`**  
Каждая запись добавляется новой строкой.

---

# Часть 6. TelephonyActivity

## Описание
`TelephonyActivity` предназначена для получения и отображения информации о текущих сотовых сетях устройства с использованием `TelephonyManager`.

Activity определяет тип сети и выводит технические параметры базовых станций.

---

## Функционал
- Получение данных о сотовых ячейках
- Поддержка сетей:
  - **GSM (2G)**
  - **LTE (4G)**
  - **NR (5G)** (Android 10+)
- Отображение параметров сети и уровня сигнала
- Обновление данных по кнопке
- Корректная обработка разрешений

---

## Используемые разрешения
```xml
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

---

Отображаемые параметры

### LTE (4G)
- CI, EARFCN, PCI, TAC
- MCC / MNC
- RSRP, RSRQ, RSSI, RSSNR, CQI


### GSM (2G)
- CID, LAC, ARFCN, BSIC
- MCC / MNC
- Уровень сигнала (dBm)

### 5G NR
- NCI, NRARFCN, PCI, TAC
- MCC / MNC
- SS-RSRP, SS-RSRQ, SS-SINR

---

## ПР9  
### Работа с сокетами (ZMQ). Передача данных от Android к PC

В рамках данной практической работы реализована передача данных между Android-приложением и серверным приложением на ПК с использованием библиотеки **ZeroMQ (ZMQ)**. Android-приложение выступает в роли **клиента**, подключающегося к серверу по TCP.  

---

## Цель работы

- Изучить работу с сокетами ZeroMQ на Android
- Реализовать клиентскую часть клиент-серверного приложения
- Настроить передачу данных по TCP
- Закрепить работу с потоками в Android
- Освоить работу с ветками Git и merge request

---

## Общая схема взаимодействия:  
[ Android-приложение ]  
|  
| ZMQ (REQ)  
| "Hello from Android!"  
v  
[ Python-сервер (PC) ]  
|  
| ZMQ (REP)  
| "Hello from Server!"  
v  
[ Консоль + файл логов ]  


---

## Клиентская часть (Android)

Клиентская часть реализована в Android-приложении на языке **Kotlin** с использованием библиотеки **ZeroMQ (JeroMQ)**.

Функционал Android-приложения:
- подключение к серверу по TCP;
- отправка строки `"Hello from Android!"`;
- получение ответа от сервера `"Hello from Server!"`;
- вывод отправленных и полученных данных на экран;
- запуск передачи данных по нажатию кнопки;
- выполнение сетевых операций в отдельном потоке.

Передача данных осуществляется в отдельном `Thread`, что предотвращает блокировку UI-потока.

---

## Используемые технологии

- Kotlin
- Android SDK
- ZeroMQ (JeroMQ)
- Тип сокета: `REQ`
- Протокол передачи: TCP

---

## Условия выполнения

- Android-устройство и компьютер находятся в одной сети
- Серверная часть запущена до подключения клиента
- Передача данных инициируется нажатием кнопки в Android-приложении

---

## ПР10  
### Сериализация. Data-классы. Формирование JSON

В рамках практической работы №10 реализовано формирование структуры данных (DTO) на Android, сериализация в JSON и периодическая отправка на сервер по **ZeroMQ** (TCP). На стороне сервера данные принимаются, выводятся в консоль и сохраняются в файл.  

---

## 6.1 Схема архитектуры программного комплекса (от ПР6 до ПР10)

Начиная с получения Location (ПР6) и заканчивая передачей JSON-пакетов (ПР10):

[ Android ]  
├─ LocationActivity (ПР6) -> получение координат  
├─ TelephonyActivity (ПР7) -> получение информации о сотах (GSM/LTE/NR)  
├─ TelemetryBuilder (ПР10) -> сбор DTO -> JSON  
└─ SerializationActivity (ПР10) -> отправка JSON каждые 1s (ZMQ REQ)  
|  
| tcp://<SERVER_IP>:5555  
v  
[ Desktop Server (Python + ZMQ) ]  
├─ ZMQ REP socket -> прием JSON  
├─ вывод в консоль  
├─ счетчик пакетов  
└─ сохранение каждого пакета в файл (лог)  

---

## 6.2 Описание архитектуры клиента (Android)

### Структура проекта (файловая система)

Клиентская часть реализована в виде Android-приложения на языке Kotlin.  
Основные компоненты, задействованные в ПР10:  
app/  
├─ java/com/example/calculator/  
│ ├─ SerializationActivity.kt  
│ ├─ TelemetryBuilder.kt  
│ ├─ TelemetryDtos.kt  
│ ├─ LocationActivity.kt  
│ └─ TelephonyActivity.kt  
└─ res/layout/  
└─ activity_serialization.xml  

- `SerializationActivity.kt` — управление процессом передачи данных (Start / Stop);  
- `TelemetryBuilder.kt` — сбор данных Location и CellInfo, формирование DTO и JSON;  
- `TelemetryDtos.kt` — data-классы для сериализации;  
- `LocationActivity.kt` — получение координат устройства;  
- `TelephonyActivity.kt` — получение данных о сотовых сетях;  
- `activity_serialization.xml` — пользовательский интерфейс для запуска передачи.  

---

### Архитектура с точки зрения потоков (Thread)

В приложении используются два типа потоков:

- **UI-поток**
  - обработка нажатий кнопок;
  - отображение состояния передачи;
  - вывод информации пользователю.

- **Фоновый поток**
  - запускается при нажатии кнопки Start;
  - реализован через `Thread { sendLoop() }`;
  - с периодичностью **1 секунда**:
    - формирует структуру DTO;
    - сериализует данные в JSON;
    - отправляет данные на сервер;
    - получает ответ от сервера.

Использование фонового потока предотвращает блокировку UI.

---

### Endpoint соединения с сервером

Для передачи данных используется ZeroMQ.

- Протокол: **TCP**
- Тип сокета: **REQ**
- Endpoint сервера: tcp://<SERVER_IP>:5555

---

## 6.3 Описание архитектуры сервера (Python)

### Структура проекта (файловая система)

Серверная часть реализована на языке Python с использованием ZeroMQ.  
backend-server/  
├─ examples/  
│ └─ server_zmq.py  
└─ README.md  

- `server_zmq.py` — сервер ZeroMQ, принимающий данные от Android-клиента.

---

### Архитектура с точки зрения потоков (Thread)

Сервер реализован в виде однопоточного приложения:

- создаётся ZMQ-сокет типа **REP**;
- сервер постоянно ожидает входящие сообщения;
- при получении сообщения:
  - данные выводятся в консоль;
  - увеличивается счётчик принятых пакетов;
  - данные сохраняются в файл (лог).

---

### Endpoint’ы сервера

Сервер прослушивает входящие подключения по адресу: tcp://0.0.0.0:5555  

Используемый тип сокета:
- **REP** — сервер отвечает каждому клиентскому запросу.

---

# ПР13  
## Backend + PostgreSQL + Визуализация сигналов

---

## Цель работы

Доработать серверное приложение для:
- сохранения телеметрии в базу данных PostgreSQL;
- обработки входящих данных от Android-приложения;
- отображения текущих параметров в GUI;
- построения графиков уровня сигнала для нескольких сот.

---

## Общая архитектура

[ Android-приложение ]  
├─ сбор данных (Location + Telephony)  
├─ формирование JSON (Telemetry)  
└─ отправка по ZMQ (REQ)  

↓ tcp://<SERVER_IP>:5555  

[ C++ Backend Server ]  
├─ ZMQ REP socket  
├─ парсинг JSON  
├─ обновление GUI (текущие значения)  
├─ построение графиков (ImGui + ImPlot)  
└─ запись в PostgreSQL  

↓  

[ PostgreSQL ]  
├─ telemetry_packets  
└─ telemetry_cells  

---

## Структура базы данных

### Таблица telemetry_packets

Хранит основные данные пакета:

```sql
CREATE TABLE telemetry_packets (
    id SERIAL PRIMARY KEY,
    ts_client_ms BIGINT,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    altitude DOUBLE PRECISION,
    accuracy DOUBLE PRECISION,
    provider TEXT
);
```

### Таблица telemetry_cells

Хранит данные по каждой соте (PCI):

```sql
CREATE TABLE telemetry_cells (
    id SERIAL PRIMARY KEY,
    packet_id INTEGER REFERENCES telemetry_packets(id),
    radio TEXT,
    pci INTEGER,
    rsrp DOUBLE PRECISION,
    rsrq DOUBLE PRECISION,
    rssi DOUBLE PRECISION,
    sinr DOUBLE PRECISION,
    asu INTEGER
);
```

---

## Логика работы сервера  
  
При получении каждого JSON-пакета:  
	1. Парсится блок данных (location, traffic, cells);  
	2. В таблицу telemetry_packets добавляется запись;  
	3. Получается packet_id;  
	4. Для каждой соты:  
		1. извлекаются параметры (PCI, RSRP, RSSI, SINR и др.);  
		2. создаётся запись в telemetry_cells.  

---

## Работа GUI  
  
GUI построен на Dear ImGui + ImPlot.  
  
### Отображаемые данные:  
	1. текущая локация (lat, lon, alt);  
	2. параметры сигнала;  
	3. JSON последнего пакета;  
	4. информация о сети.  
  
---
  
## Графики сигналов  
  
Реализованы графики:  
	1. RSRP;  
	2. RSSI;  
	3. SINR;  

---

Принцип построения графиков  
	1. при поступлении новых данных значения добавляются в массивы;  
	2. для каждого PCI ведётся отдельная история;  
	3. по оси X - номер измерения (Sample);  
	4. по оси Y - уровень сигнала.  

---

## Поддержка нескольких сот  
  
Если в одном пакете приходят несколько сот:  

PCI 7 → RSRP = -100  
PCI 13 → RSRP = -89  
  
то:  
	1. на графике отображаются несколько линий одновременно;  
	2. каждая линия соответствует своему PCI;  
	3. линии отображаются разными цветами;  
	4. значения отображаются параллельно по одной оси X.  

---

## Легенда графика  

	1. в легенде отображаются все обнаруженные PCI;  
	2. каждый PCI соответствует своей линии;  
	3. позволяет отслеживать поведение каждой соты во времени.  

---

## Особенности реализации

	1. графики строятся в реальном времени;  
	2. данные берутся из оперативной памяти (не из БД);  
	3. БД используется только для хранения;  
	4. реализована фильтрация некорректных значений (например, 2147483647);  
	5. поддерживается работа с несколькими типами сетей (GSM, LTE, NR).  

---

# ПР14  
## OpenStreetMap + Отрисовка карты + Тайловая система

---

## Цель работы

Реализовать отображение OpenStreetMap-карты в backend-приложении с использованием:
- Dear ImGui;
- ImPlot;
- OpenGL;
- libcurl;
- тайловой системы OpenStreetMap.

Также реализовать:
- загрузку PNG-тайлов с OSM-сервера;
- локальное кэширование изображений;
- динамическую подгрузку тайлов;
- изменение zoom;
- перемещение карты в реальном времени;
- отображение GPS-координат Android-устройства на карте.

---

## Общая архитектура

[ Android-приложение ]  
├─ DriveTestService  
├─ GPS + Telephony telemetry  
└─ ZMQ REQ client  

↓ tcp://<SERVER_IP>:5555  

[ C++ Backend ]  
├─ ZMQ REP server  
├─ OSM tile loader  
├─ PNG decoder (stb_image)  
├─ OpenGL texture manager  
├─ ImGui + ImPlot GUI  
└─ Tile cache system  

↓  

[ OpenStreetMap Tile Server ]  
https://tile.openstreetmap.org/{z}/{x}/{y}.png

---

## Используемые технологии

- C++
- OpenGL
- GLFW
- Dear ImGui
- ImPlot
- libcurl
- stb_image
- OpenStreetMap
- Mercator Projection

---

## Принцип работы тайловой системы

OpenStreetMap использует тайловую систему.

Каждый тайл:
- имеет размер `256x256` пикселей;
- определяется координатами:
  - `x`
  - `y`
  - `zoom`.

Для отображения карты координаты GPS преобразуются:
- из Latitude / Longitude
- в Tile X / Tile Y.

---

## Преобразование координат

### Longitude → tileX

```cpp
tileX = (lon_deg + 180.0) / 360.0 * (1 << zoom);
```

### Latitude → tileY

```cpp
lat_rad = lat_deg * M_PI / 180.0;

tileY = (
    1.0 - log(
        tan(lat_rad) +
        1.0 / cos(lat_rad)
    ) / M_PI
) / 2.0 * (1 << zoom);
```

---

## Загрузка тайлов

Backend-приложение автоматически определяет:
- текущий центр карты;
- текущий zoom;
- размер окна ImGui.

После этого вычисляется необходимое количество тайлов.

Например:

- окно `512x512`
- размер тайла `256x256`

=> требуется минимум `2x2 = 4 тайла`.

---

## Получение PNG через libcurl

Для загрузки изображений используется libcurl.

Пример endpoint:

```text
https://tile.openstreetmap.org/10/603/385.png
```

Алгоритм:
1. Формируется URL;
2. curl отправляет HTTP GET;
3. PNG сохраняется во временный буфер;
4. stb_image декодирует изображение;
5. создаётся OpenGL texture.

---

## Кэширование тайлов

Все загруженные изображения сохраняются локально.

Структура директорий:

```text
build/
└─ zoom/
   └─ x/
      └─ y.png
```

Пример:

```text
build/10/603/385.png
```

---

## Логика работы кэша

При запросе тайла:

1. Проверяется наличие файла:
   ```cpp
   build/z/x/y.png
   ```

2. Если файл существует:
   - тайл загружается с диска;

3. Иначе:
   - выполняется загрузка через curl;
   - PNG сохраняется локально;
   - создаётся texture OpenGL.

Это уменьшает:
- количество HTTP-запросов;
- задержки отображения;
- нагрузку на OSM-сервер.

---

## Работа с OpenGL texture

После декодирования PNG:
- изображение преобразуется в RGBA-массив;
- создаётся texture OpenGL;
- texture отображается внутри ImPlot.

Используются функции:

```cpp
glGenTextures()
glBindTexture()
glTexImage2D()
```

---

## Отображение карты

Карта отображается в отдельном окне ImGui:

```cpp
ImGui::Begin("OSM Map");
```

Внутри окна:
- отображаются тайлы;
- отображается центр карты;
- отображается текущий zoom;
- рисуется точка текущего GPS-положения.

---

## Перемещение карты

Реализовано:
- изменение центра карты;
- изменение zoom;
- динамическая подгрузка новых тайлов.

При изменении:
- координат;
- масштаба;
- размера окна

backend автоматически пересчитывает:
- список видимых тайлов;
- координаты отображения.

---

## Поддержка GPS-позиции

После получения телеметрии:
- сервер обновляет текущие координаты;
- GUI автоматически перемещает центр карты;
- отображается маркер положения устройства.

---

## Структура backend-проекта

```text
backend/
├─ gui_thread.cpp
├─ server_thread.cpp
├─ osm_math.cpp
├─ osm_math.h
├─ tile_manager.cpp
├─ tile_manager.h
├─ curl_func.cpp
├─ curl_func.h
├─ main.cpp
└─ CMakeLists.txt
```

---

## Назначение файлов

| Файл | Назначение |
|---|---|
| `gui_thread.cpp` | GUI, ImGui, ImPlot, отображение карты |
| `server_thread.cpp` | ZMQ сервер и обработка телеметрии |
| `osm_math.cpp` | Пересчёт координат и Mercator projection |
| `tile_manager.cpp` | Управление тайлами и кэшем |
| `curl_func.cpp` | HTTP-загрузка PNG через curl |
| `main.cpp` | Точка входа |

---

## Асинхронная загрузка тайлов

Загрузка изображений выполняется:
- в отдельном потоке;
- без остановки GUI.

Это позволяет:
- не блокировать интерфейс;
- плавно перемещать карту;
- догружать тайлы "на лету".

---

# ПР15  
## Генерация Heatmap (тепловой карты)

---

## Цель работы

Реализовать построение тепловой карты качества сигнала мобильной сети на основе:
- GPS-координат;
- параметров LTE / NR;
- телеметрии Android-приложения.

Тепловая карта строится:
- поверх OpenStreetMap;
- в реальном времени;
- на основе метода IDW (Inverse Distance Weighting).

---

## Используемые параметры сигнала

Heatmap может строиться по:
- RSRP;
- RSRQ;
- RSSI;
- SINR;
- Altitude.

Для LTE поддерживается разделение:
- по EARFCN;
- по PCI.

---

## Используемые технологии

- C++
- Dear ImGui
- ImPlot
- OpenGL
- PostgreSQL
- OpenStreetMap
- IDW interpolation

---

## Общая архитектура

[ Android-приложение ]  
├─ GPS telemetry  
├─ LTE / NR параметры  
└─ ZMQ transmission  

↓  

[ Backend Server ]  
├─ PostgreSQL storage  
├─ HeatPoint generator  
├─ IDW interpolation  
├─ Heatmap texture builder  
└─ OpenGL renderer  

↓  

[ Heatmap Overlay ]  
поверх OpenStreetMap

---

## Принцип работы Heatmap

Сервер получает:
- координаты устройства;
- параметры сигнала.

После этого:
1. формируются точки измерений;
2. выполняется интерполяция;
3. создаётся цветовая карта покрытия.

---

## Метод IDW

Для интерполяции используется метод:

Inverse Distance Weighting (IDW)

Идея метода:
- ближайшие точки влияют сильнее;
- дальние точки влияют слабее.

---

## Формула IDW

```text
V(x) = Σ( wi * vi ) / Σ(wi)
```

где:

- `vi` — значение сигнала в точке;
- `wi` — вес точки.

Вес вычисляется:

```text
wi = 1 / d^p
```

где:
- `d` — расстояние;
- `p` — степень влияния.

---

## Ограничение радиуса интерполяции

Для уменьшения артефактов используется ограничение:

```text
10 - 40 метров
```

Если измерение находится слишком далеко:
- точка не участвует в вычислении.

Это позволяет:
- избежать ложных значений;
- повысить точность карты покрытия.

---

## Формирование heat points

После получения телеметрии сервер:
- извлекает координаты;
- извлекает RSRP / RSSI / SINR;
- создаёт heat point.

Пример структуры:

```cpp
struct HeatPoint {
    double lat;
    double lon;
    double value;
    int pci;
    int earfcn;
};
```

---

## Работа с PostgreSQL

Точки могут:
- сразу отображаться в GUI;
- дополнительно загружаться из PostgreSQL.

Используются таблицы:
- `telemetry_packets`
- `telemetry_cells`

---

## Генерация изображения heatmap

Для каждой области карты:
1. создаётся RGBA-buffer;
2. выполняется IDW-интерполяция;
3. вычисляется цвет пикселя;
4. создаётся texture OpenGL.

---

## Цветовая схема

### RSRP

| Качество сигнала | Значение |
|---|---|
| Excellent | > -80 dBm |
| Good | -80 ... -90 dBm |
| Fair | -90 ... -100 dBm |
| Poor | -100 ... -110 dBm |
| No Signal | < -110 dBm |

---

## Цвета heatmap

Используются следующие цвета:

| Уровень | Цвет |
|---|---|
| Excellent | Красный |
| Good | Оранжевый |
| Fair | Жёлтый |
| Poor | Синий |
| No Signal | Не отображается |

---

## Отображение heatmap

Heatmap отображается:
- поверх тайлов OpenStreetMap;
- внутри окна ImGui;
- как OpenGL texture.

---

## GUI-интерфейс

В интерфейсе реализовано:
- переключение критерия:
  - RSRP
  - RSSI
  - RSRQ
  - SINR
  - Altitude

- выбор EARFCN;
- изменение zoom;
- включение / выключение heatmap.

---

## Производительность

Вычисления heatmap выполняются:
- в отдельном потоке;
- независимо от GUI.

Это позволяет:
- избежать зависаний интерфейса;
- обновлять карту в реальном времени;
- продолжать обработку телеметрии.

---

## Кэширование heatmap

Сгенерированные изображения сохраняются:

```text
build/zoom/x/y.png
```

или:

```text
build/heatmap.png
```

При повторном отображении:
- изображение загружается из кэша;
- повторное вычисление не требуется.

---

# Установка зависимостей

---

## Android-клиент

### Требования

- Android Studio
- Android SDK
- Kotlin
- Gradle
- Android API 29+

---

## Используемые библиотеки Android

### ZeroMQ (JeroMQ)

В `build.gradle`:

```gradle
implementation 'org.zeromq:jeromq:0.5.3'
```

---

## Разрешения Android

В `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

---

# Backend (C++)

---

## Linux / WSL Ubuntu

### Установка основных зависимостей

```bash
sudo apt update
```

---

### C++ toolchain

```bash
sudo apt install build-essential cmake ninja-build -y
```

---

### OpenGL + GLFW

```bash
sudo apt install libglfw3-dev libgl1-mesa-dev -y
```

---

### ZeroMQ

```bash
sudo apt install libzmq3-dev cppzmq-dev -y
```

---

### PostgreSQL client

```bash
sudo apt install libpq-dev -y
```

---

### CURL

```bash
sudo apt install libcurl4-openssl-dev -y
```

---

### pkg-config

```bash
sudo apt install pkg-config -y
```

---

## MSYS2 / MinGW64 (Windows)

Установка выполняется через MSYS2 MinGW64.

---

### Обновление системы

```bash
pacman -Syu
```

---

### Установка toolchain

```bash
pacman -S mingw-w64-x86_64-toolchain -y
```

---

### OpenGL + GLFW

```bash
pacman -S mingw-w64-x86_64-glfw -y
```

---

### ZeroMQ

```bash
pacman -S mingw-w64-x86_64-zeromq -y
```

---

### cppzmq

```bash
pacman -S mingw-w64-x86_64-cppzmq -y
```

---

### PostgreSQL

```bash
pacman -S mingw-w64-x86_64-postgresql -y
```

---

### CURL

```bash
pacman -S mingw-w64-x86_64-curl -y
```

---

### pkgconf

```bash
pacman -S mingw-w64-x86_64-pkgconf -y
```

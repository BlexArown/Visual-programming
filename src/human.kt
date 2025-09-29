import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

open class Human(
    private var fullName: String,
    private var age: Int,
    private var speed: Double
) {
    private var x: Double = 0.0
    private var y: Double = 0.0

    // случ движение
    open fun move(dt: Double) {
        val angle = Random.nextDouble(0.0, 2 * PI)
        x += speed * dt * cos(angle)
        y += speed * dt * sin(angle)
    }

    // геттеры и сеттеры
    fun getName(): String = fullName
    fun setName(newName: String) { fullName = newName }

    fun getAge(): Int = age
    fun setAge(newAge: Int) { age = newAge }

    fun getSpeed(): Double = speed
    fun setSpeed(newSpeed: Double) { speed = newSpeed }

    fun getX(): Double = x
    fun getY(): Double = y

    protected fun setX(newX: Double) { x = newX }
    protected fun setY(newY: Double) { y = newY }
}

// наследник Driver
class Driver(
    fullName: String,
    age: Int,
    speed: Double
) : Human(fullName, age, speed) {

    override fun move(dt: Double) {
        // прямолинейно по оси X
        setX(getX() + getSpeed() * dt)
    }
}

fun main() {
    val people = listOf(
        Human(fullName = "Вера Геннадьевна Дроздова", age = 26, speed = 2.0),
        Human(fullName = "Андреев Андрей Валерьевич", age = 27, speed = 2.5),
        Human(fullName = "Пастухов Александр Андреевич", age = 19, speed = 1.3),
        Driver(fullName = "Жолар Тимур Батрудинович (водитель)", age = 31, speed = 3.2)
    )

    val simulationTime = 5.0
    val step = 1.0

    println("Начало симуляции движения (${people.size} объектов)\n")

    val threads = mutableListOf<Thread>()

    for (person in people) {
        val t = Thread {
            var time = 0.0
            while (time <= simulationTime) {
                person.move(step)
                println("[${Thread.currentThread().name}] ${person.getName()} → (%.2f, %.2f)"
                    .format(person.getX(), person.getY()))
                time += step
                Thread.sleep((300..900).random().toLong()) // разная задержка
            }
        }
        t.name = person.getName()
        threads.add(t)
    }

    // запускаем все потоки
    threads.forEach { it.start() }
    // ждём завершения всех
    threads.forEach { it.join() }

    println("\nСимуляция завершена!")
    println("\n\n\nФинальные позиции:")

    println("=".repeat(60))
    for (person in people) {
        println("%-40s (%.2f, %.2f)".format(person.getName(), person.getX(), person.getY()))
    }
    println("=".repeat(60))
}

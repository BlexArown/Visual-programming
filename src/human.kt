import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Human(
    private var fullName: String,
    private var age: Int,
    private var speed: Double
) {
    private var x: Double = 0.0
    private var y: Double = 0.0

    // случ угол и шаг
    fun move(dt: Double) {
        val angle = Random.nextDouble(0.0, 2 * PI)
        x = x + speed * dt * cos(angle)
        y = y + speed * dt * sin(angle)
    }

    // геттер и сеттер ( ну как Гензель и Греттель но по другому)
    fun getName(): String = fullName
    fun setName(newName: String) { fullName = newName }

    fun getAge(): Int = age
    fun setAge(newAge: Int) { age = newAge }

    fun getSpeed(): Double = speed
    fun setSpeed(newSpeed: Double) { speed = newSpeed }

    fun getX(): Double = x
    fun getY(): Double = y
}

fun main() {
    // массив людей
    val people = arrayOf(
        Human(fullName = "Вера Геннадьевна Дроздова", age = 26, speed = 2.0),
        Human(fullName = "Андреев Андрей Валерьевич", age = 27, speed = 2.5),
        Human(fullName = "Пастухов Александр Андреевич", age = 19, speed = 1.3),
        Human(fullName = "Нейдорф Павел Яковлевич", age = 21, speed = 2.2),
        Human(fullName = "Багазий Виктория Викторовна", age = 19, speed = 0.7),
        Human(fullName = "Мунти Ольга Витальевна", age = 53, speed = 0.2),
        Human(fullName = "Силко Оленна Минтивна", age = 43, speed = 1.2),
        Human(fullName = "Вейд Максим Эдуардович", age = 23, speed = 2.1),
        Human(fullName = "Жолар Тимур Батрудинович", age = 31, speed = 3.2)
    )

    val simulationTime = 5.0   // всего секунд
    val step = 1.0             // шаг моделир
    var time = 0.0

    println("Начало симуляции движения (${people.size} человек)")

    while (time <= simulationTime) {
        println("\nВремя: $time секунд")

        for (person in people) {
            person.move(step)
            println("${person.getName()} на позиции (%.2f, %.2f)".format(person.getX(), person.getY()))
        }

        time += step
    }

    println("\nСимуляция завершена!")
}

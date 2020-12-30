import java.util.*

class FixedStack<E>(private val maxSize: Int = 100) : ArrayDeque<E>() {

    init {
        if (maxSize <= 0) {
            throw IllegalArgumentException("maxSize must grater than 0!")
        }
    }

    override fun addFirst(e: E) {
        if (size == maxSize) {
            removeLast()
        }
        super.addFirst(e)
    }
}
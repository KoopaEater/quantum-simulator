package dk.maxkandersen.random

import kotlin.random.Random

class StandardRandomStrategy : RandomStrategy {
    override fun next(): Double {
        return Random.nextDouble()
    }
}
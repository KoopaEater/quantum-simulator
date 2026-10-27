package dk.maxkandersen.random

class FixedRandomStrategy(val output: Double) : RandomStrategy {
    override fun next(): Double = output
}
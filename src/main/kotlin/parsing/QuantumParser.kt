package dk.maxkandersen.parsing

import dk.maxkandersen.action.gate.CHGate
import dk.maxkandersen.action.gate.CPGate
import dk.maxkandersen.action.gate.CSGate
import dk.maxkandersen.action.gate.CTGate
import dk.maxkandersen.action.gate.CXGate
import dk.maxkandersen.action.gate.CZGate
import dk.maxkandersen.action.gate.DoubleQubitGate
import dk.maxkandersen.action.gate.Gate
import dk.maxkandersen.action.gate.HGate
import dk.maxkandersen.action.gate.IDGate
import dk.maxkandersen.action.gate.PGate
import dk.maxkandersen.action.gate.SGate
import dk.maxkandersen.action.gate.SingleQubitGate
import dk.maxkandersen.action.gate.TGate
import dk.maxkandersen.action.gate.XGate
import dk.maxkandersen.action.gate.ZGate
import dk.maxkandersen.action.measure.FullMeasurement
import dk.maxkandersen.action.measure.Measurement
import dk.maxkandersen.action.measure.SingleQubitMeasurement
import dk.maxkandersen.circuit.QCircuit
import dk.maxkandersen.parsing.paths.PathStrategy
import dk.maxkandersen.parsing.paths.RelativePathStrategy
import dk.maxkandersen.parsing.exception.NumArgsException
import dk.maxkandersen.parsing.exception.EmptyFileException
import dk.maxkandersen.parsing.exception.InvalidArgException
import dk.maxkandersen.parsing.exception.NumQubitsException
import dk.maxkandersen.parsing.exception.UnknownCommandException

class QuantumParser(val pathStrategy: PathStrategy = RelativePathStrategy()) {

    fun read(filename: String): QCircuit {
        return pathStrategy.open(filename).useLines(::parse)
    }

    private fun parse(lines: Sequence<String>): QCircuit {
        val it = lines.iterator()
        if (!it.hasNext()) {
            throw EmptyFileException()
        }

        val line0 = it.next()
        val numQubits = line0.trim().split(" ").singleOrNull()?.toIntOrNull()
            ?: throw NumQubitsException()
        if (numQubits < 1) throw NumQubitsException()

        val circuit = QCircuit(numQubits)
        var lineNum = 1
        while (it.hasNext()) {
            val line = it.next().trim()
            lineNum++

            if (line.startsWith("#")) continue

            val split = line.split(Regex("\\s+")) // Split on whitespace
            val command = split[0]
            val args = split.drop(1)

            if (command.isBlank()) continue

            when (command) {
                "I" -> circuit.addAction(parseSimpleSingleGate(command, args, IDGate(), numQubits, lineNum))
                "X" -> circuit.addAction(parseSimpleSingleGate(command, args, XGate(), numQubits, lineNum))
                "Z" -> circuit.addAction(parseSimpleSingleGate(command, args, ZGate(), numQubits, lineNum))
                "H" -> circuit.addAction(parseSimpleSingleGate(command, args, HGate(), numQubits, lineNum))
                "S" -> circuit.addAction(parseSimpleSingleGate(command, args, SGate(), numQubits, lineNum))
                "T" -> circuit.addAction(parseSimpleSingleGate(command, args, TGate(), numQubits, lineNum))
                "P" -> {
                    if (args.size != 2) throw NumArgsException(lineNum, command, "2", args.size)
                    val theta = args[0].toDoubleOrNull() ?: throw InvalidArgException(lineNum, command, 1, "a number (angle in radians)", args[0])
                    circuit.addAction(parseSimpleSingleGate(command, args, PGate(theta), numQubits, lineNum, argOffset = 1))
                }
                "CNOT", "CX" -> circuit.addAction(parseSimpleDoubleGate(command, args, CXGate(), numQubits, lineNum))
                "CZ" -> circuit.addAction(parseSimpleDoubleGate(command, args, CZGate(), numQubits, lineNum))
                "CH" -> circuit.addAction(parseSimpleDoubleGate(command, args, CHGate(), numQubits, lineNum))
                "CT" -> circuit.addAction(parseSimpleDoubleGate(command, args, CTGate(), numQubits, lineNum))
                "CS" -> circuit.addAction(parseSimpleDoubleGate(command, args, CSGate(), numQubits, lineNum))
                "CP" -> {
                    if (args.size != 3) throw NumArgsException(lineNum, command, "3", args.size)
                    val theta = args[0].toDoubleOrNull() ?: throw InvalidArgException(lineNum, command, 1, "a number (angle in radians)", args[0])
                    circuit.addAction(parseSimpleDoubleGate(command, args, CPGate(theta), numQubits, lineNum, argOffset = 1))
                }
                "MEASURE" -> circuit.addAction(parseMeasurement(args, numQubits, lineNum))
                else -> throw UnknownCommandException(lineNum, command)
            }

        }
        return circuit
    }

    private fun getQubit(args: List<String>, arg: Int, numQubits: Int, command: String, lineNum: Int): Int {
        val qubit = args[arg].toIntOrNull() ?: throw InvalidArgException(lineNum, command, arg+1, "a number (qubit)", args[arg])
        if (qubit !in 0 until numQubits) throw InvalidArgException(lineNum, command, arg+1, "a number (qubit) between 0 and $numQubits (numQubits)", args[arg])
        return qubit
    }

    private fun parseSimpleSingleGate(command: String, args: List<String>, gate: SingleQubitGate, numQubits: Int, lineNum: Int, argOffset: Int = 0): Gate {
        if (args.size != 1 + argOffset) throw NumArgsException(lineNum, command, "${1+argOffset}", args.size)
        val qubit = getQubit(args, 0 + argOffset, numQubits, command, lineNum)
        return gate.expandWithID(qubit, numQubits)
    }
    private fun parseSimpleDoubleGate(command: String, args: List<String>, gate: DoubleQubitGate, numQubits: Int, lineNum: Int, argOffset: Int = 0): Gate {
        if (args.size != 2 + argOffset) throw NumArgsException(lineNum, command, "${2+argOffset}", args.size)
        val qubit1 = getQubit(args, 0 + argOffset, numQubits, command, lineNum)
        val qubit2 = getQubit(args, 1 + argOffset, numQubits, command, lineNum)
        if (qubit1 == qubit2) throw InvalidArgException(lineNum, command, 1 + argOffset, "unique qubits", "$qubit1 for both")
        return gate.generateMultiGate(qubit1, qubit2, numQubits)
    }
    private fun parseMeasurement(args: List<String>, numQubits: Int, lineNum: Int): Measurement {
        if (args.size !in 0..1) throw NumArgsException(lineNum, "MEASURE", "0-1", args.size)
        if (args.isEmpty()) {
            return FullMeasurement(numQubits)
        } else {
            val qubit = getQubit(args, 0, numQubits, "MEASURE", lineNum)
            return SingleQubitMeasurement(qubit, numQubits)
        }
    }

}
package dk.maxkandersen.file

import dk.maxkandersen.action.gate.CHGate
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
import dk.maxkandersen.file.exception.NumArgsException
import dk.maxkandersen.file.exception.EmptyFileException
import dk.maxkandersen.file.exception.InvalidArgException
import dk.maxkandersen.file.exception.NumQubitsException
import dk.maxkandersen.file.exception.UnknownCommandException

class QuantumFileReader(val pathStrategy: PathStrategy = RelativePathStrategy()) {

    fun read(filename: String): QCircuit {
        return pathStrategy.open(filename).useLines(::parse)
    }

    private fun parse(lines: Sequence<String>): QCircuit {
        val it = lines.iterator()
        if (!it.hasNext()) {
            throw EmptyFileException()
        }

        val line0 = it.next()
        val numQubits = line0.split(" ").singleOrNull()?.toIntOrNull()
            ?: throw NumQubitsException()

        val circuit = QCircuit(numQubits)
        var lineNum = 1
        while (it.hasNext()) {
            val line = it.next()
            lineNum++

            if (line.trim().startsWith("#")) continue

            val split = line.split(" ")
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
                    val qubit = getQubit(args, 1, numQubits, command, lineNum)
                    circuit.addAction(PGate(theta).expandWithID(qubit, numQubits))
                }
                "CNOT", "CX" -> circuit.addAction(parseSimpleDoubleGate(command, args, CXGate(), numQubits, lineNum))
                "CZ" -> circuit.addAction(parseSimpleDoubleGate(command, args, CZGate(), numQubits, lineNum))
                "CH" -> circuit.addAction(parseSimpleDoubleGate(command, args, CHGate(), numQubits, lineNum))
                "CT" -> circuit.addAction(parseSimpleDoubleGate(command, args, CTGate(), numQubits, lineNum))
                "CS" -> circuit.addAction(parseSimpleDoubleGate(command, args, CSGate(), numQubits, lineNum))
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

    private fun parseSimpleSingleGate(command: String, args: List<String>, gate: SingleQubitGate, numQubits: Int, lineNum: Int): Gate {
        if (args.size != 1) throw NumArgsException(lineNum, command, "1", args.size)
        val qubit = getQubit(args, 0, numQubits, command, lineNum)
        return gate.expandWithID(qubit, numQubits)
    }
    private fun parseSimpleDoubleGate(command: String, args: List<String>, gate: DoubleQubitGate, numQubits: Int, lineNum: Int): Gate {
        if (args.size != 2) throw NumArgsException(lineNum, command, "2", args.size)
        val qubit1 = getQubit(args, 0, numQubits, command, lineNum)
        val qubit2 = getQubit(args, 1, numQubits, command, lineNum)
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
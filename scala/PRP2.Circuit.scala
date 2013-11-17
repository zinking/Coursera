package simulations

import common._

class Wire {
  private var sigVal = false
  private var actions: List[Simulator#Action] = List()

  def getSignal: Boolean = sigVal
  
  def setSignal(s: Boolean) {
    if (s != sigVal) {
      sigVal = s
      actions.foreach(action => action())
    }
  }

  def addAction(a: Simulator#Action) {
    actions = a :: actions
    a()
  }
}

abstract class CircuitSimulator extends Simulator {

  val InverterDelay: Int
  val AndGateDelay: Int
  val OrGateDelay: Int

  def probe(name: String, wire: Wire) {
    wire addAction {
      () => afterDelay(0) {
        println(
          "  " + currentTime + ": " + name + " -> " +  wire.getSignal)
      }
    }
  }

  def inverter(input: Wire, output: Wire) {
    def invertAction() {
      val inputSig = input.getSignal
      afterDelay(InverterDelay) { output.setSignal(!inputSig) }
    }
    input addAction invertAction
  }

  def andGate(a1: Wire, a2: Wire, output: Wire) {
    def andAction() {
      val a1Sig = a1.getSignal
      val a2Sig = a2.getSignal
      afterDelay(AndGateDelay) { output.setSignal(a1Sig & a2Sig) }
    }
    a1 addAction andAction
    a2 addAction andAction
  }

  //
  // to complete with orGates and demux...
  //

  def orGate2(a1: Wire, a2: Wire, output: Wire) {
     def orAction() {
      val ia1 , ia2 , ia1ia2 = new Wire
      inverter( a1, ia1 )
      inverter( a2, ia2 )
      andGate( ia1,ia2, ia1ia2)
      inverter( ia1ia2, output )
      //afterDelay(OrGateDelay) { output.setSignal(a1Sig | a2Sig) }
    }
    a1 addAction orAction
    a2 addAction orAction
  }
  
  def orGate(a1: Wire, a2: Wire, output: Wire) {
    // a or b = inv( inv( a and b ) ) = inv( inv a and inv b )
    def orAction() {
      val a1Sig = a1.getSignal
      val a2Sig = a2.getSignal
      afterDelay(OrGateDelay) { output.setSignal(a1Sig | a2Sig) }
    }
    a1 addAction orAction
    a2 addAction orAction
  }

  def demux(in: Wire, c: List[Wire], out: List[Wire]) {
    def demux0(in: Wire, c: List[Wire]): List[Wire] = {
      c match {
        case x :: xs =>
          val xbar, left, right = new Wire
          inverter(x, xbar)
          andGate(xbar, in, left)
          andGate(x,    in, right)
          demux0(left, xs) ::: demux0(right, xs)
        case Nil =>
          List(in)
      }
    }
    connectWires(demux0(in, c), out)
  }

  def connectWires(input: List[Wire], output: List[Wire]) {
    input.zip(output).foreach(pair => connectWire(pair._1, pair._2))
  }

  def connectWire(input: Wire, output: Wire) {
    input addAction(() => output.setSignal(input.getSignal))
  }

}

object Circuit extends CircuitSimulator {
  val InverterDelay = 1
  val AndGateDelay = 3
  val OrGateDelay = 5

  def andGateExample {
    val in1, in2, out = new Wire
    andGate(in1, in2, out)
    probe("in1", in1)
    probe("in2", in2)
    probe("out", out)
    in1.setSignal(false)
    in2.setSignal(false)
    run

    in1.setSignal(true)
    run

    in2.setSignal(true)
    run
  }

  //
  // to complete with orGateExample and demuxExample...
  //
}

object CircuitMain extends App {
  // You can write tests either here, or better in the test class CircuitSuite.
  Circuit.andGateExample
}

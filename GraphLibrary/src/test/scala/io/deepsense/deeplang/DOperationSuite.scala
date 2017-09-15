/**
 * Copyright (c) 2015, CodiLime, Inc.
 *
 * Owner: Witold Jedrzejewski
 */

package io.deepsense.deeplang

import scala.reflect.runtime.{universe => ru}

import org.scalatest.FunSuite

import io.deepsense.deeplang.dhierarchy.DHierarchy
import io.deepsense.deeplang.parameters.ParametersSchema

object DClassesForDOperations {
  trait A extends DOperable
  class A1 extends A {
    override def equals(any: Any) = any.isInstanceOf[A1]
  }
  class A2 extends A {
    override def equals(any: Any) = any.isInstanceOf[A2]
  }
}

object DOperationForPortTypes {
  import DClassesForDOperations._
  class SimpleOperation extends DOperation1To1[A1, A2] {
    override protected def _execute(context: ExecutionContext)(t0: A1): A2 = ???
  }
}

class DOperationSuite extends FunSuite {

  test("It is possible to implement simple operations") {
    import DClassesForDOperations._

    case class IntParam(i: Int) extends ParametersSchema

    class PickOne extends DOperation2To1[A1, A2, A] {
      override protected def _execute(context: ExecutionContext)(t1: A1, t2: A2): A = {
        val intParam = parameters.asInstanceOf[IntParam]
        if (intParam.i % 2 == 1) t1 else t2
      }
    }

    val firstPicker: DOperation = new PickOne
    firstPicker.parameters = IntParam(1)
    val secondPicker: DOperation = new PickOne
    secondPicker.parameters = IntParam(2)

    val input = Vector(new A1, new A2)
    assert(firstPicker.execute(new ExecutionContext)(input) == Vector(new A1))
    assert(secondPicker.execute(new ExecutionContext)(input) == Vector(new A2))

    val h = new DHierarchy
    h.registerDOperable[A1]()
    h.registerDOperable[A2]()
    val context = new InferContext(h)

    val knowledge = Vector[DKnowledge[DOperable]](DKnowledge(new A1), DKnowledge(new A2))
    assert(firstPicker.inferKnowledge(context)(knowledge) == Vector(DKnowledge(new A1, new A2)))
  }

  test("It is possible to override knowledge inferring in DOperation") {
    import DClassesForDOperations._

    class GeneratorOfA extends DOperation0To1[A] {
      override protected def _execute(context: ExecutionContext)(): A = ???
      override protected def _inferKnowledge(context: InferContext)(): DKnowledge[A] = {
        new DKnowledge(new A1, new A2)
      }
    }

    val generator: DOperation = new GeneratorOfA

    val h = new DHierarchy
    h.registerDOperable[A1]()
    h.registerDOperable[A2]()
    val context = new InferContext(h)

    assert(generator.inferKnowledge(context)(Vector()) == Vector(DKnowledge(new A1, new A2)))
  }

  test("Getting types required in input port") {
    import DOperationForPortTypes._
    val op = new SimpleOperation
    assert(op.inPortTypes == Vector(ru.typeTag[DClassesForDOperations.A1]))
  }

  test("Getting types required in output port") {
    import DOperationForPortTypes._
    val op = new SimpleOperation
    assert(op.outPortTypes == Vector(ru.typeTag[DClassesForDOperations.A2]))
  }
}

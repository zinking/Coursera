package simulations

import math.random

class EpidemySimulator extends Simulator {

  def randomBelow(i: Int) = (random * i).toInt

  protected[simulations] object SimConfig {
    val population: Int = 10
    val roomRows: Int = 8
    val roomColumns: Int = 8
    val prevalence:Float = 0.2f
    val transmissibility:Float = 0.4f
    val movingDelay:Int = 5
    val deathproability:Float = 0.25f

    // to complete: additional parameters of simulation
  }

  import SimConfig._

  
  val persons: List[Person] = {
    val pps = List.tabulate(population)(n=> new Person(n));  // to complete: construct list of persons
    pps.filter( p => { p.id < population * prevalence} ).foreach( p=>{
	    p.getSick
	})
	pps
  }

  persons.foreach( p=>{
    movePerson( p )
  })
  


  
  def isRoomInfectious( r: Int, c: Int ):Boolean = {
    val ps = persons.filter(p => { 
      ( p.col == c ) &&
      ( p.row == r ) &&
      ( p.sick || p.dead)
    })
    return !ps.isEmpty
  }
  
  def movePerson( p: Person ){
    def infectAction() {
      val pp:Float = randomBelow( 10 ) / 10f;
      if ( pp < transmissibility && p.isInfectable ) p.infected = true
      afterDelay( 6 ) { p.getSick }
      afterDelay( 14 ){ p.probablyDie }
      afterDelay( 16 ){ p.getImmune }
      afterDelay( 18 ){ p.getHealthy }
    }
    
    def moveAction() {
      val moveDelay = randomBelow( movingDelay )
      afterDelay(moveDelay) { p.randomMove }
      p addAction infectAction
      //p addAction moveAction
      afterDelay(moveDelay + 1) { 
        //println( "insert movement for ", p )
        movePerson(p) 
      } 
      //one difficulty: move after move
    }
    if( !p.dead ) p addAction moveAction
  }
  
  


  class Person (val id: Int) {
    var infected = false
    var sick = false
    var immune = false
    var dead = false

    // demonstrates random number generation
    var row: Int = randomBelow(roomRows)
    var col: Int = randomBelow(roomColumns)
    
    
    private var actions: List[Simulator#Action] = List()

    override def toString():String={
      //s"P[$id] [$row,$col]"
      s"P[$id] [$infected]"
    }
    //
    // to complete with simulation logic
    //
	def addAction(a: Simulator#Action) {
	  actions = a :: actions
	  a()
	}
    
    def isInfectable():Boolean={
      return !dead && !immune && !sick && !immune && isRoomInfectious(row, col)
    }
    
    def getSick{
      sick = true
    }
    
    def getImmune{
      if(dead) return
      immune = true
    }
    
    def getHealthy{
    	if(dead) return
	    infected = false
	    sick = false
	    immune = false
	    dead = false
    }
    
    def probablyDie{
      val pp:Float = randomBelow( 10 ) / 10f;
      if( pp < deathproability ){
        dead = true
      }
    }
    
    def randomMove{
      if(dead) return
      val left 	= (-1,0)
      val right = (1,0)
      val up 	= (0,-1)
      val down  = (0,1)
      val ds = List( left,right,up,down)
      val rr = randomBelow(4)
      val nrow = ( row + ds(rr)._2 + roomRows ) % roomRows
      val ncol = ( col + ds(rr)._1 + roomColumns ) % roomColumns
      if( !isRoomInfectious(nrow, ncol) ){
        row = nrow
        col = ncol 
      }
    	  
    }
  }
}

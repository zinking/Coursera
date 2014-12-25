makeCacheMatrix <- function(x = matrix()) {
	i <- NULL
    set <- function(y) {
        x <<- y
        i <<- NULL
    }	
    get <- function() {
        i <<- solve(x)
    }
	setInverse <- function(inverse) i <<- inverse
    getInverse <- function() i
    list(	set=set, get=get, 
		setInverse=setInverse, 
		getInverse=getInverse)
}


cacheSolve <- function(x, ...) {
    cr <- x$getInverse()
    if (!is.null(cr)) {	
       message("getting cached matrix reverse")
       return(cr)
    } 
    data <- x$get()
    x$setInverse(data)
    data
}
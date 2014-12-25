rankhospital <- function(state, outcome, num = "best") {
## Read outcome data
	hdata <- read.csv("outcome-of-care-measures.csv", colClasses = "character")
## Check that state and outcome are valid
## Return hospital name in that state with the given rank

	if( !( state %in% hdata$State) ){
		stop('invalid state')
	}
	validOutcome=c('heart attack', 'heart failure', 'pneumonia')
	optm=NULL
 	optm[['heart attack']] = 11
	optm[['heart failure']] = 17
	optm[['pneumonia']] = 23
	if( !( outcome %in% validOutcome) ){
		stop('invalid outcome')
	}
	if( !(num %in% c("best","worst")) && !is.numeric(num) ){
		stop('invalid num')
	}
	col <- optm[[outcome]]
	hdata[,col] <- as.numeric(hdata[,col])
	shdata <- subset(hdata, hdata$State==state, c(2,col))
	cshdata <- shdata[complete.cases(shdata),]
	scshdata <- cshdata[order(cshdata[,2],cshdata[,1]),]
## 30-day death rate
	ncount<-length(scshdata[,1])
	if( num == "best" ){
		scshdata[1,1]
	}
	else if( num=="worst"){
		scshdata[ncount,1]
	}
	else if( num>ncount){
		NA
	}
	else if( num>0){
		scshdata[num,1]
	}
	
}
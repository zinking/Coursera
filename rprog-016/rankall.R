rankhospitalWithData <- function(hdata,state, outcome, num = "best") {
## Read outcome data
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


rankall <- function(outcome, num = "best") {
## Read outcome data
 hdata <- read.csv("outcome-of-care-measures.csv", colClasses = "character")

## Check that state and outcome are valid
	validOutcome=c('heart attack', 'heart failure', 'pneumonia')
	if( !( outcome %in% validOutcome) ){
		stop('invalid outcome')
	}
	if( !(num %in% c("best","worst")) && !is.numeric(num) ){
		stop('invalid num')
	}

## For each state, find the hospital of the given rank
## Return a data frame with the hospital names and the
## (abbreviated) state name
	datastates <- hdata$State
	allstates <- datastates[order(datastates)]
	state <- allstates[!duplicated(allstates)]
	hospital<-c()
	for( st in state){
		hp<-rankhospitalWithData(hdata,st,outcome,num)
		hospital<-rbind(hospital,c(hp))
	}
	
	data.frame(row.names=states,hospital,state)
}



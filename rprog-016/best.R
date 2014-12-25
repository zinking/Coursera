best <- function(state, outcome) {
## Read outcome data
	hdata <- read.csv("outcome-of-care-measures.csv", colClasses = "character")
## Check that state and outcome are valid
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
	col <- optm[[outcome]]
	hdata[,col] <- as.numeric(hdata[,col])
	shdata <- subset(hdata, hdata$State==state, c(2,col))
	cshdata <- shdata[complete.cases(shdata),]
	scshdata <- cshdata[order(cshdata[,2],cshdata[,1]),]
## Return hospital name in that state with lowest 30-day death
## rate
	scshdata[1,1]
}
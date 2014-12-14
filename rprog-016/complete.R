complete <- function(directory, id = 1:332) {
        ## 'directory' is a character vector of length 1 indicating
        ## the location of the CSV files

        ## 'id' is an integer vector indicating the monitor ID numbers
        ## to be used
        
        ## Return a data frame of the form:
        ## id nobs
        ## 1  117
        ## 2  1041
        ## ...
        ## where 'id' is the monitor ID number and 'nobs' is the
        ## number of complete cases
    col_classes=c("numeric","numeric")
    col_names=c("id","nobs")
	pv <- read.table(text="",colClasses=col_classes,col.names=col_names)
	for( iid in id ){
		filename <- sprintf("%s/%03d.csv",directory,iid)
		data <- read.csv(filename, header=TRUE )
		nobs <- dim(data[complete.cases(data),])[1]
		pv <- rbind(pv,c(iid,nobs))
	}
	colnames(pv)<-c("id","nobs")
	pv
}
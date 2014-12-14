corr <- function(directory, threshold = 0) {
        ## 'directory' is a character vector of length 1 indicating
        ## the location of the CSV files

        ## 'threshold' is a numeric vector of length 1 indicating the
        ## number of completely observed observations (on all
        ## variables) required to compute the correlation between
        ## nitrate and sulfate; the default is 0

        ## Return a numeric vector of correlations
	pv <- c()
	for( iid in 1:332 ){
		filename <- sprintf("%s/%03d.csv",directory,iid)
		data <- read.csv(filename, header=TRUE )
		nob <- data[complete.cases(data),]
		nobs <- dim(nob)[1]
		if( nobs > threshold ){
			pv<-c(pv,cor( nob[['sulfate']], nob[['nitrate']]))
		}
	}
	pv
}
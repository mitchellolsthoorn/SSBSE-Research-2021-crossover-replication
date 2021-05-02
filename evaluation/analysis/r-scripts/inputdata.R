library(dplyr)

getMetricsForSubjects <- function(){
  raw_results <- read.csv("subjects/components/code-metrics/metrics.csv", stringsAsFactors = FALSE)
  return(raw_results)
}
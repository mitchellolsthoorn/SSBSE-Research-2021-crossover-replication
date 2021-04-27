library(dplyr)

raw_results <- read.csv("../components/code-metrics/metrics.csv", stringsAsFactors = FALSE)

df <- raw_results %>%
  group_by(project,class) %>%
  summarise(avg_ccn = mean(wmc),
            sum_string_args= sum(string_args),
            sum_number_args= sum(number_args))


write.csv(df,"../components/code-metrics/class-level-metrics.csv",row.names = FALSE)

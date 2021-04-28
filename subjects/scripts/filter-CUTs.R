library(dplyr)

raw_results <- read.csv("../components/code-metrics/metrics.csv", stringsAsFactors = FALSE)

df <- raw_results %>%
  group_by(project,class) %>%
  summarise(avg_ccn = mean(wmc),
            sum_string_args= sum(string_args),
            sum_number_args= sum(number_args))


df <- df %>% filter(sum_string_args != 0 & sum_number_args != 0) %>%
  arrange(desc(avg_ccn))

finnal_df = head(df, 100)

finnal_df <- finnal_df %>% select(project, class)





write.csv(finnal_df,"../projects.csv",row.names = FALSE)

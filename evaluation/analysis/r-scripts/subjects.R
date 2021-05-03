library(readr)

source('evaluation/analysis/r-scripts/inputdata.r')
source('evaluation/analysis/r-scripts/table.r')

raw_results <- getMetricsForSubjects()
df <- raw_results %>%
  group_by(project,class) %>%
  summarise(avg_ccn = mean(wmc),
            sum_string_args= sum(string_args),
            sum_number_args= sum(number_args))
### Collect the 100 CUTs ###
# Filter out non-interesting classes: test classes, non trivial classes, classes without strings and numbers input arguments
df <- df %>% filter(sum_string_args != 0 & sum_number_args != 0) %>%
  filter(!grepl("Test", class, fixed=TRUE) &
           class != "org.apache.commons.rng.core.RandomAssert") %>%
  arrange(desc(avg_ccn)) # Sort the selected classes according to their average ccn

final_df = head(df, 100) # Select top 10

# Save the selected CUTs to projects.csv
subjects_df <- final_df %>% select(project, class)
write_csv(subjects_df,"subjects/projects.csv")


### Generate a table that shows selected cases specifications ###
# Calculate average, standard deviation, max an min for three componenets: CCN. number of string arguments, and number of number arguments
cases_specifications <- final_df %>%
  group_by(project) %>%
  summarise(number_of_selected_classes = n(), ccn_avg = mean(avg_ccn), ccn_sd = sd(avg_ccn), ccn_max = max(avg_ccn), ccn_min = min(avg_ccn),
            string_args_avg = mean(sum_string_args), string_args_sd = sd(sum_string_args), string_args_max = max(sum_string_args), string_args_min = min(sum_string_args),
            number_args_avg = mean(sum_number_args), number_args_sd = sd(sum_number_args), number_args_max = max(sum_number_args), number_args_min = min(sum_number_args))

# Since the number of selected cases from LOGGING project is 1, the standard deviation for this specific case is NA. We replace it with "-".
#cases_specifications$ccn_sd <- ifelse(is.na(cases_specifications$ccn_sd),"-",cases_specifications$ccn_sd)
#cases_specifications$string_args_sd <- ifelse(is.na(cases_specifications$string_args_sd),"-",cases_specifications$string_args_sd)
#cases_specifications$number_args_sd <- ifelse(is.na(cases_specifications$number_args_sd),"-",cases_specifications$number_args_sd)
# Generate and save the final table
generateSubjectsTable(cases_specifications)





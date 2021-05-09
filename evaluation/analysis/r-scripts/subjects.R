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
  filter(
           # ignore test classes
           !grepl("Test", class, fixed=TRUE) &
           class != "org.apache.commons.rng.core.RandomAssert" &
           class != "RealFunctionValidation$ApplicationProperties" &
           class != "org.apache.commons.math3.fitting.leastsquares.StatisticalReferenceDataset" &
           class != "org.apache.commons.math3.optimization.general.StatisticalReferenceDataset" &
           class != "org.apache.commons.math3.optim.nonlinear.vector.jacobian.StatisticalReferenceDataset" &
           class != "org.apache.commons.math3.geometry.partitioning.RegionDumper$TreeDumper" &
           class != "org.apache.commons.lang3.CharUtilsPerfRun" &
           class != "org.apache.commons.math3.ml.neuralnet.sofm.City" &
           class != "org.apache.commons.geometry.spherical.oned.Point1S" &
           class != "org.apache.commons.geometry.examples.jmh.euclidean.VectorPerformance$VectorInputBase" &
           # ignore Math's userguide classes
           !grepl("math3.userguide", class, fixed=TRUE) &
           # ignore private inner classes (EvoSuite cannot cover them at all)
           class != "org.apache.commons.text.StrMatcher$StringMatcher" &
           class != "org.apache.commons.rng.sampling.distribution.MarsagliaTsangWangDiscreteSampler$MarsagliaTsangWangBase64Int8DiscreteSampler" &
           class != "org.apache.commons.rng.sampling.distribution.MarsagliaTsangWangDiscreteSampler$MarsagliaTsangWangBase64Int32DiscreteSampler" &
           class != "org.apache.commons.rng.sampling.distribution.MarsagliaTsangWangDiscreteSampler$MarsagliaTsangWangBase64Int16DiscreteSampler" &
           class != "org.apache.commons.lang3.time.FastDateParser$NumberStrategy" &
           class != "org.apache.commons.lang3.time.FastDateParser$ISO8601TimeZoneStrategy" &
           class != "org.apache.commons.lang3.time.FastDateParser$CopyQuotedStrategy" &
           class != "org.apache.commons.lang3.text.StrMatcher$StringMatcher") %>%
  filter(project != "Stemmer") %>%
  arrange(desc(avg_ccn)) # Sort the selected classes according to their average ccn

final_df = head(df, 100) # Select top 100

# Add snowball stemmer classes
stemmer_cases <- raw_results %>%
  filter(project == "Stemmer")



stemmer_cases <- stemmer_cases %>%
  group_by(project,class) %>%
  summarise(avg_ccn = mean(wmc),
            sum_string_args= sum(string_args),
            sum_number_args= sum(number_args))

final_df <-rbind(final_df, stemmer_cases)


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





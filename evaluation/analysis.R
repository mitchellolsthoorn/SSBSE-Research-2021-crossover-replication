library(magrittr)
library(dplyr)
library(tidyr)

options(scipen=999)

source("evaluation/combine_csv_files.R")
source("evaluation/compare_strategies.R")

# Collect results from runner
results <- get_results("results")

# Show statistics for collected results
results %>% dplyr::count(configuration_id, round)  %>% dplyr::arrange(round)
results %>% dplyr::count(configuration_id, TARGET_CLASS) %>% dplyr::arrange(TARGET_CLASS)

# Find classes that don't produce results for both approaches
results_coverage_pivot <- results %>% dplyr::select(TARGET_CLASS, configuration_id, Coverage) %>% tidyr::pivot_wider(names_from = configuration_id, values_from = Coverage)
results_filtered_1 <- results_coverage_pivot %>% dplyr::filter(is.na(default_120) | default_120 == "" | is.null(crossover_100_120))

# Find classes that don't achieve any coverage on the baseline
results_filtered_2 <- results %>% dplyr::filter(configuration_id == "default_120" & Coverage == 0)
results_filtered_2 <- unique(results_filtered_2$TARGET_CLASS)

# Filter classes that don't match the requirements of the study
results_filtered <- results %>% dplyr::filter(!(TARGET_CLASS %in% c("org.apache.commons.math3.util.BigReal", "org.apache.commons.math3.dfp.DfpDec", "org.apache.commons.lang3.time.FastDateParser$CaseInsensitiveTextStrategy", "org.apache.commons.geometry.examples.tutorials.bsp.BSPTreeSVGWriter$TreeStructureVisitor", "org.apache.commons.cli.Option$Builder")))

results_filtered %<>% mutate(configuration.id = sub("default_120", "SPX", configuration.id))
results_filtered %<>% mutate(configuration.id = sub("crossover_120", "HMX", configuration.id))

# Compare the different startegies
branch_results_120 <- compare_strategies("crossover_100_120", "default_120", "BranchCoverage", results_filtered)
cbranch_results_120 <- compare_strategies("crossover_100_120", "default_120", "CBranchCoverage", results_filtered)
line_results_120 <- compare_strategies("crossover_100_120", "default_120", "LineCoverage", results_filtered)
weak_results_120 <- compare_strategies("crossover_100_120", "default_120", "WeakMutationScore", results_filtered)
strong_results_120 <- compare_strategies("crossover_100_120", "default_120", "MutationScore", results_filtered)
output_results_120 <- compare_strategies("crossover_100_120", "default_120", "OutputCoverage", results_filtered)

branch_results_120 %<>% dplyr::mutate(diff = average1 - average2)
cbranch_results_120 %<>% dplyr::mutate(diff = average1 - average2)
line_results_120 %<>% dplyr::mutate(diff = average1 - average2)
weak_results_120 %<>% dplyr::mutate(diff = average1 - average2)
strong_results_120 %<>% dplyr::mutate(diff = average1 - average2)
output_results_120 %<>% dplyr::mutate(diff = average1 - average2)

mean(branch_results_120$diff)
mean(dplyr::filter(branch_results_120, p_values <= 0.05)$diff)
mean(line_results_120$diff)
mean(dplyr::filter(line_results_120, p_values <= 0.05)$diff)

dplyr::filter(branch_results_120, p_values > 0.05 | is.nan(p_values))
branch_results_win <- dplyr::filter(branch_results_120, p_values <= 0.05 & a12 > 0.5)
branch_results_cuts <- branch_results_win$valid_CUTs
dplyr::filter(branch_results_120, p_values <= 0.05 & a12 <= 0.5)

max(dplyr::filter(branch_results_120, p_values <= 0.05 & a12 > 0.5)$diff)
mean(dplyr::filter(branch_results_120, p_values <= 0.05 & a12 > 0.5 & str_detect(valid_CUTs, "org.tartarus.snowball.ext"))$diff)

dplyr::filter(cbranch_results_120, p_values > 0.05 | is.nan(p_values))
dplyr::filter(cbranch_results_120, p_values <= 0.05 & a12 > 0.5)
dplyr::filter(cbranch_results_120, p_values <= 0.05 & a12 <= 0.5)

dplyr::filter(line_results_120, p_values > 0.05 | is.nan(p_values))
line_results_win <- dplyr::filter(line_results_120, p_values <= 0.05 & a12 > 0.5)
line_results_cuts <- line_results_win$valid_CUTs
dplyr::filter(line_results_120, p_values <= 0.05 & a12 <= 0.5)

max(dplyr::filter(line_results_120, p_values <= 0.05 & a12 > 0.5)$diff)
mean(dplyr::filter(line_results_120, p_values <= 0.05 & a12 > 0.5 & str_detect(valid_CUTs, "org.tartarus.snowball.ext"))$diff)

dplyr::filter(weak_results_120, p_values > 0.05 | is.nan(p_values))
dplyr::filter(weak_results_120, p_values <= 0.05 & a12 > 0.5)
dplyr::filter(weak_results_120, p_values <= 0.05 & a12 <= 0.5)

max(dplyr::filter(weak_results_120, p_values <= 0.05 & a12 > 0.5)$diff)
mean(dplyr::filter(weak_results_120, p_values <= 0.05 & a12 > 0.5)$diff)
mean(dplyr::filter(weak_results_120, p_values <= 0.05 & a12 > 0.5 & str_detect(valid_CUTs, "org.tartarus.snowball.ext"))$diff)
dplyr::filter(weak_results_120, p_values <= 0.05 & a12 > 0.5 & valid_CUTs %in% branch_results_cuts)
dplyr::filter(weak_results_120, p_values <= 0.05 & a12 > 0.5 & !(valid_CUTs %in% branch_results_cuts) & !(valid_CUTs %in% line_results_cuts))

dplyr::filter(strong_results_120, p_values > 0.05 | is.nan(p_values))
dplyr::filter(strong_results_120, p_values <= 0.05 & a12 > 0.5)
dplyr::filter(strong_results_120, p_values <= 0.05 & a12 <= 0.5)

max(dplyr::filter(strong_results_120, p_values <= 0.05 & a12 > 0.5)$diff)
mean(dplyr::filter(strong_results_120, p_values <= 0.05 & a12 > 0.5)$diff)
mean(dplyr::filter(strong_results_120, p_values <= 0.05 & a12 > 0.5 & str_detect(valid_CUTs, "org.tartarus.snowball.ext"))$diff)
dplyr::filter(strong_results_120, p_values <= 0.05 & a12 > 0.5 & valid_CUTs %in% branch_results_cuts)
dplyr::filter(strong_results_120, p_values <= 0.05 & a12 > 0.5 & !(valid_CUTs %in% branch_results_cuts) & !(valid_CUTs %in% line_results_cuts))

dplyr::filter(output_results_120, p_values > 0.05 | is.nan(p_values))
dplyr::filter(output_results_120, p_values <= 0.05 & a12 > 0.5)
dplyr::filter(output_results_120, p_values <= 0.05 & a12 <= 0.5)

# Plot results

library(ggplot2)

ggplot(results_filtered, aes(x = configuration.id, y = Generations)) +
  geom_boxplot() +
  stat_summary(fun=mean, geom="point", shape=23, size=2, fill = 'white') +
  theme_bw() +
  labs(x = "Crossover", y = "Generations")

ggplot(results_filtered, aes(x = configuration.id, y = BranchCoverage)) +
  geom_boxplot() +
  stat_summary(fun=mean, geom="point", shape=23, size=2, fill = 'white') +
  theme_bw() +
  labs(x = "Crossover", y = "Branch coverage")

ggsave("evaluation/branch-coverage.pdf", width = 12, height = 12, units = "cm")

ggplot(results_filtered, aes(x = configuration.id, y = LineCoverage)) +
  geom_boxplot() +
  stat_summary(fun=mean, geom="point", shape=23, size=2, fill = 'white') +
  theme_bw() +
  labs(x = "Crossover", y = "Line coverage")

ggsave("evaluation/line-coverage.pdf", width = 12, height = 12, units = "cm")

ggplot(results_filtered, aes(x = configuration.id, y = WeakMutationScore)) +
  geom_boxplot() +
  stat_summary(fun=mean, geom="point", shape=23, size=2, fill = 'white') +
  theme_bw() +
  labs(x = "Crossover", y = "Weak mutation score")

ggsave("evaluation/weak-mutation-score.pdf", width = 12, height = 12, units = "cm")

ggplot(results_filtered, aes(x = configuration.id, y = MutationScore)) +
  geom_boxplot() +
  stat_summary(fun=mean, geom="point", shape=23, size=2, fill = 'white') +
  theme_bw() +
  labs(x = "Crossover", y = "Mutation score")

ggsave("evaluation/mutation-score.pdf", width = 12, height = 12, units = "cm")

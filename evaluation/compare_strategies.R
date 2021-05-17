library(effsize)

compare_strategies <-function(baseline, strategy, dimension, data) {
  # 
  # :param: baseline: EvoSuite + fuzzer
  # :param: strategy: EvoSuite vanilla
  # :param: dimension: can be coverage or length
  # :param: data: the 
  #
  # :return a data frame with the pvalue, cliff delta and magnitude
  # 
  p_values <- c()
  average1 <- c()
  average2 <- c()
  a12 <- c()
  a12_estimate <- c()
  valid_CUTs <- c()
  
  data.baseline = data[ data$configuration_id==baseline, ]
  data.strategy = data[ data$configuration_id==strategy, ]
  
  CUTs <- unique(data$TARGET_CLASS)
  
  for (class in CUTs) {
    subset_baseline <- data.baseline[data.baseline$TARGET_CLASS == class,]
    subset_strategy <- data.strategy[data.strategy$TARGET_CLASS == class,]
    if (nrow(subset_baseline) > 0 & nrow(subset_strategy) > 0) {
      values_baseline <- subset_baseline[,dimension]
      values_strategy <- subset_strategy[,dimension]
      average1 <- c(average1, median(values_baseline))
      average2 <- c(average2, median(values_strategy))
      
      # wilcoxon test 
      p_values <- c(p_values, wilcox.test(values_baseline, values_strategy, alternative = 'two.sided', paired = F, exact=T)$p.value)
      res_delta <- VD.A(values_baseline, values_strategy, paired = F)
      
      # effect size
      a12 <- c(a12, res_delta$estimate)
      a12_estimate <- c(a12_estimate, as.character(res_delta$magnitude))
      valid_CUTs <- c(valid_CUTs, class)
    }
  }
  df <- data.frame(valid_CUTs, average1, average2, p_values, a12, a12_estimate)
  return(df)
}

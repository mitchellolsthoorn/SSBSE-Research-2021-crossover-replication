generateSubjectsTable <- function(table_data){
  #Print the table
  outputFile <- "evaluation/analysis/latex/tables/projects-table.tex"
  unlink(outputFile)
  # Redirect cat outputs to file
  sink(outputFile, append = TRUE, split = TRUE)
  cat("\\begin{tabular}{ l r | r@{\\hskip 0.08in}r@{\\hskip 0.08in}r@{\\hskip 0.08in}r | r@{\\hskip 0.08in}r@{\\hskip 0.08in}r@{\\hskip 0.08in}r | r@{\\hskip 0.08in}r@{\\hskip 0.08in}r@{\\hskip 0.08in}r }\n")
  cat("\\hline", "\n")
  cat("\\textbf{Project}", "&", 
      "\\textbf{\\#}", "&",
      "\\multicolumn{4}{c}{\\textbf{CCN}}", "&", 
      "\\multicolumn{4}{c}{\\textbf{String parameter}}", "&", 
      "\\multicolumn{4}{c}{\\textbf{Number parameter}}")
  cat(" \\\\", "\n")
  cat(" ", "&", 
      " ", "&", 
      "$\\overline{\\text{cc}}$", "&", "$\\sigma$", "&", "min", "&", "max", "&",
      "$\\overline{\\text{str-par}}$", "&", "$\\sigma$", "&", "min", "&", "max", "&",
      "$\\overline{\\text{nr-par}}$", "&", "$\\sigma$", "&", "min", "&", "max")
  cat(" \\\\", "\n")
  cat("\\hline", "\n")
  # Loop on rows of table_data dataframe
  for(row in 1:(nrow(table_data)-1)){
    project = table_data[[row, 'project']]
    #Project
    cat(table_data[[row, 'project']], "&", 
        table_data[[row, 'number_of_selected_classes']], "&")
    #CCN
    cat(formatC(table_data[[row, 'ccn_avg']], digits=1, format="f", big.mark = ','), "&")
    
    if (project == "Logging"){
      cat("-", "&")
    }else{
      cat(formatC(table_data[[row, 'ccn_sd']], digits=1, format="f", big.mark = ','), "&")
    }
    
    cat(formatC(table_data[[row, 'ccn_max']], digits=1, format="f", big.mark = ','), "&", 
        formatC(table_data[[row, 'ccn_min']], digits=1, format="f", big.mark = ','), "&")
    
    # String 
    cat(formatC(table_data[[row, 'string_args_avg']], digits=1, format="f", big.mark = ','), "&")
    
    if (project == "Logging"){
      cat("-", "&")
    }else{
      cat(formatC(table_data[[row, 'string_args_sd']], digits=1, format="f", big.mark = ','), "&")
    }
    
    cat(formatC(table_data[[row, 'string_args_max']], digits=1, format="f", big.mark = ','), "&", 
        formatC(table_data[[row, 'string_args_min']], digits=1, format="f", big.mark = ','), "&")
    
    # Number 
    cat(formatC(table_data[[row, 'number_args_avg']], digits=1, format="f", big.mark = ','), "&")
    
    if (project == "Logging"){
      cat("-", "&")
    }else{
      cat(formatC(table_data[[row, 'number_args_sd']], digits=1, format="f", big.mark = ','), "&")
    }
    
    cat(formatC(table_data[[row, 'number_args_max']], digits=1, format="f", big.mark = ','), "&", 
        formatC(table_data[[row, 'number_args_min']], digits=1, format="f", big.mark = ','))
    cat(" \\\\", "\n")
  }
  cat("\\hline", "\n")
  cat("\\end{tabular}")
  # Restore cat outputs to console
  sink()
}
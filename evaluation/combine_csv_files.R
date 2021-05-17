library(stringr)

get_experiment_configuration_ids <- function(results_directory) {
  configuration_ids <- list.dirs(path = results_directory, full.names = FALSE, recursive = FALSE)
  return(configuration_ids)
}

get_experiment_project_ids <- function(results_directory, configuration_id) {
  project_ids <- list.dirs(path = file.path(results_directory, configuration_id), full.names = FALSE, recursive = FALSE)
  return(project_ids)
}

get_experiment_classes <- function(results_directory, configuration_id, project_id) {
  classes <- list.dirs(path = file.path(results_directory, configuration_id, project_id), full.names = FALSE, recursive = FALSE)
  return(classes)
}

get_experiment_round_numbers <- function(results_directory, configuration_id, project_id, class) {
  round_numbers <- list.dirs(path = file.path(results_directory, configuration_id, project_id, class, "reports"), full.names = FALSE, recursive = FALSE)
  return(round_numbers)
}

get_experiment_statistics_file <- function(results_directory, configuration_id, project_id, class, round_number) {
  files <-list.files(file.path(results_directory, configuration_id, project_id, class, "reports", round_number), recursive=TRUE, full.names = TRUE)
  print(files)
  csv_files = files[str_detect(files, ".csv")]
  statistics <- read.csv(csv_files[1], header=TRUE)

  statistics$configuration.id=configuration_id
  statistics$project.id=project_id
  statistics$class=class
  statistics$round=round_number

  return(statistics)
}

get_results <- function(results_directory) {
  for (configuration_id in get_experiment_configuration_ids(results_directory)) {
    for (project_id in get_experiment_project_ids(results_directory, configuration_id)) {
      for (class in get_experiment_classes(results_directory, configuration_id, project_id)) {
        for (round_number in get_experiment_round_numbers(results_directory, configuration_id, project_id, class)) {
          if (exists("res")) {
            res <- rbind(get_experiment_statistics_file(results_directory, configuration_id, project_id, class, round_number), res)
          } else {
            res <- get_experiment_statistics_file(results_directory, configuration_id, project_id, class, round_number)
          }
        }
      }
    }
  }

  return(res)
}

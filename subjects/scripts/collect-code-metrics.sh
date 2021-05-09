components_csv=$1

# Check input CSV file
[ ! -f $components_csv ] && { die "$components_csv file not found"; }

script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
code_dir="$script_dir/../components/code"
code_metrics_dir="$script_dir/../components/code-metrics"
CK_jar="$script_dir/../tools/CK.jar"
first_dir=$(pwd)
OLDIFS=$IFS
IFS=,


header_passed="0"


while read component repo c_type latest_tag latest_commit
do
    # skip the title row
    if [[ "$header_passed" -eq "0" ]]; then
        header_passed="1"
        continue
    fi

    echo "Calculate code metrics for $component"
    component_dir="$code_dir/commons-""$(tr [A-Z] [a-z] <<< "$component")"
    report_dir="$code_metrics_dir/commons-""$(tr [A-Z] [a-z] <<< "$component")"
    rm -rf $report_dir
    mkdir $report_dir
    cd $report_dir
    java -jar "$CK_jar" "$component_dir" true
    cd $first_dir

done < $components_csv


component_dir="$code_dir/snowball-stemmer/src/java/org/tartarus/snowball/ext/"
report_dir="$code_metrics_dir/snowball-stemmer"
rm -rf $report_dir
mkdir $report_dir
cd $report_dir
java -jar "$CK_jar" "$component_dir" true
cd $first_dir
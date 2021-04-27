# ATTENTION: Run this script after clone-and-build script. For more info please read the README file.

components_csv=$1

# Check input CSV file
[ ! -f $components_csv ] && { die "$components_csv file not found"; }

script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
code_dir="$script_dir/../components/code"
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

    echo "Collecting $component's jar files"
    component_dir="$code_dir/commons-""$(tr [A-Z] [a-z] <<< "$component")"
    cd $component_dir
    jar_files=( $(find . -type f -name "*.jar") )
    IFS=$'\n'
    rm -rf "$first_dir/subjects/bins/$component"
    mkdir "$first_dir/subjects/bins/$component"
    for jar_file in ${jar_files[@]}
    do
        if [[ $jar_file == *-sources.jar ]] || [[ $jar_file == *-tests.jar ]] || [[ $jar_file == *-javadoc.jar ]]; then
            continue
        fi
        echo "Detected Jar file >>> "$jar_file
        echo "Copy to $first_dir/subjects/bins/$component"
        cp $jar_file "$first_dir/subjects/bins/$component/$(basename $jar_file)"
    done
    IFS=,
    cd $first_dir
done < $components_csv
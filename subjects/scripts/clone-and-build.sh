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
    echo "Next component is $component"

    ##### Cloning #####
    # Check if the component is already cloned
    component_dir="$code_dir/commons-""$(tr [A-Z] [a-z] <<< "$component")"
    if [ -d "$component_dir" ]; then
        echo "$component_dir already exists"
    else
        # If the component is not cloned yet, clone it
        echo "cloning the repo from $repo ..."
        cd $code_dir
        git clone $repo
        cd $first_dir
    fi

    ##### Checkout to the latest tag #####
    cd $component_dir
    # We skip tag detection for Statistics component because it doesnt have one.
    # Instead, we use the latest commit. When we perfomed this subject collection, the latest passing commit was: e6f7da62aae6638e9305ad4c242529765b4c5821
    if [[ "$component" == "Statistics" ]]; then
        echo "Latest commit $latest_commit"
        git checkout $latest_commit
    else
        # git fetch --tags
        # latest_tag=$(git describe --tags `git rev-list --tags --max-count=1`)
        echo "Latest tag: "$latest_tag
        git checkout $latest_tag
    fi
    
    cd $first_dir

    ##### Remove component's git files #####
    cd $component_dir
        rm -rf .git/
    cd $first_dir

    ##### Build #####

    # cd $component_dir
    #     mvn package
    # cd $first_dir



done < $components_csv
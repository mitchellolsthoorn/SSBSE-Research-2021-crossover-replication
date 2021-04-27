components_csv=$1

# Check input CSV file
[ ! -f $components_csv ] && { die "$components_csv file not found"; }

script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
code_metrics_dir="$script_dir/../components/code-metrics"
first_dir=$(pwd)
#Prepare single CSV file
echo "project,file,class,method,constructor,line,cbo,wmc,rfc,loc,returnsQty,variablesQty,parametersQty,methodsInvokedQty,methodsInvokedLocalQty,methodsInvokedIndirectLocalQty,loopQty,comparisonsQty,tryCatchQty,parenthesizedExpsQty,stringLiteralsQty,numbersQty,assignmentsQty,mathOperationsQty,maxNestedBlocksQty,anonymousClassesQty,innerClassesQty,lambdasQty,uniqueWordsQty,modifiers,logStatementsQty,hasJavaDoc,string_args,number_args" > "$code_metrics_dir/metrics.csv"

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

    echo "collect $component's code metrics"
    report_dir="$code_metrics_dir/commons-""$(tr [A-Z] [a-z] <<< "$component")"
    method_file="$report_dir/method.csv"
    IFS=
    while read -r line; do
        # IFS=,
        echo ")>"$line

        IFS=',' read -ra elements <<< "$line"
        IFS='/' read -ra method_elements <<< "${elements[2]}"
        
        if [[ ${method_elements[1]} == "0" ]]; then
            continue
        fi

        IFS='[' read -ra temp <<< "${method_elements[1]}"
        number_of_args=${temp[0]}
        last_param=$((number_of_args + 2))
        number_of_string=0
        number_of_numbers=0
        for (( index=2; index<$last_param; index++ ))
        do  
            arg_type=""
            if [[ $index == "2" ]]; then
                IFS='/' read -ra rat <<< "${elements[$index]}"
                arg_type=${rat[1]:2}
                if [[ $number_of_args == "1" ]]; then
                    arg_type=${arg_type%?}
                fi
            elif [[ $index == $((last_param - 1)) ]]; then
                arg_type=${elements[$index]%??}
            else
                arg_type=${elements[$index]}
            fi

            if [[ $arg_type == "java.lang.String" ]]; then
                number_of_string=$((number_of_string + 1))
            elif [[ $arg_type == "double" ]] || [[ $arg_type == "int" ]] || [[ $arg_type == "float" ]] || [[ $arg_type == "long" ]] || [[ $arg_type == "java.lang.Integer" ]] || [[ $arg_type == "java.lang.Double" ]] || [[ $arg_type == "java.lang.Float" ]] || [[ $arg_type == "java.lang.Long" ]]; then
                number_of_numbers=$((number_of_numbers + 1))
            fi
        done
        IFS=

        echo "$component,${line%?},$number_of_string,$number_of_numbers" >> "$code_metrics_dir/metrics.csv"

    done < $method_file
    IFS=,

done < $components_csv
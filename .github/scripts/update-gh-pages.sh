#!/usr/bin/env bash

#
# Copyright contributors to the Galasa project
#
# SPDX-License-Identifier: EPL-2.0
#

#-----------------------------------------------------------------------------------------                   
#
# Set Colors
#
#-----------------------------------------------------------------------------------------                   
bold=$(tput bold)
underline=$(tput sgr 0 1)
reset=$(tput sgr0)
red=$(tput setaf 1)
green=$(tput setaf 76)
white=$(tput setaf 7)
tan=$(tput setaf 202)
blue=$(tput setaf 25)

#-----------------------------------------------------------------------------------------                   
#
# Headers and Logging
#
#-----------------------------------------------------------------------------------------                   
underline() { printf "${underline}${bold}%s${reset}\n" "$@" ; }
h1() { printf "\n${underline}${bold}${blue}%s${reset}\n" "$@" ; }
h2() { printf "\n${underline}${bold}${white}%s${reset}\n" "$@" ; }
debug() { printf "${white}[.] %s${reset}\n" "$@" ; }
info()  { printf "${white}[➜] %s${reset}\n" "$@" ; }
success() { printf "${white}[${green}✔${white}] ${green}%s${reset}\n" "$@" ; }
error() { printf "${white}[${red}✖${white}] ${red}%s${reset}\n" "$@" ; }
warn() { printf "${white}[${tan}➜${white}] ${tan}%s${reset}\n" "$@" ; }
bold() { printf "${bold}%s${reset}\n" "$@" ; }
note() { printf "\n${underline}${bold}${blue}Note:${reset} ${blue}%s${reset}\n" "$@" ; }


#-----------------------------------------------------------------------------------------                   
# Functions
#-----------------------------------------------------------------------------------------                   
function usage {
    info "Syntax: update-gh-pages.sh [OPTIONS]"
    cat << EOF
Options are:
--help
-m | --commit-message {message}
Environment variables used:
None

EOF
}

function check_exit_code () {
    # This function takes 3 parameters in the form:
    # $1 an integer value of the returned exit code
    # $2 an error message to display if $1 is not equal to 0
    if [[ "$1" != "0" ]]; then 
        error "$2" 
        exit 1  
    fi
}
#-----------------------------------------------------------------------------------------                   
# Process parameters
#-----------------------------------------------------------------------------------------       
h2 "Processing parameters"
commit_message=""
while [ "$1" != "" ]; do
    case $1 in
        -h | --help )           usage
                                exit
                                ;;
        -m | --commit-message)  shift 
                                commit_message=$1
                                ;;
        * )                     error "Unexpected argument $1"
                                usage
                                exit 1
    esac
    shift
done

if [[ "$commit_message" == "" ]]; then 
    error "Commit message was not specified. Use the --commit-message flag"
    exit 1
fi

h2 "listing the top level folder"
ls 

h2 "listing the drafts folder"
ls drafts

# h2 "tree listing"
# tree

h2 "seeing if there are any changs to commit"
is_clean=$(git status | grep "nothing to commit, working tree clean" | wc -l | xargs)
echo "is_clean=${is_clean}" 

# Only bother pushing the branch back to the origin if something has changed.

h2 "Possibly checking things in"
if [[ "$is_clean" == "1" ]]; then 
    info "Nothing has changed, so there is nothing to check-in to the gh-pages branch." -s -S
else
    echo "Adding to git staging..."
    git add .

    echo "Committing..."
    git commit -am "${commit_message}"  

    # There is a danger that a different workflow has touched gh-pages while this flow was
    # running...
    # So we can detect this, and retry up to a limit of times.
    retry_count=0
    while [ $retry_count -lt 10 ]; do

        info "Inside re-try-able loop. $retry_count"

        if [ $retry_count -ne 0 ]; then
            info "Sleeping..."
            sleep 3
        fi

        info "Pulling..."
        git pull --rebase
        if [[ $? != "0" ]]; then
            retry_count=$(( retry_count + 1 ))
            info "Pull failed. $retry_count"
        else
            info "Pushing..."
            git push
            if [[ $? != "0" ]]; then
                retry_count=$(( retry_count + 1 ))
                info "Push failed. $retry_count"
            else
                # It worked. Jump out of the loop.
                retry_count=100
                info "Push worked."
            fi
        fi

    done
fi


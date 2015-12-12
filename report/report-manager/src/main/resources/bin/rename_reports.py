import os
import os.path
import re
import sys

USAGE = "python rename_reports <YYYY-MM>"

def main():
    
    print("Renaming report files")
    
    # Get raw date from command line args
    if len(sys.argv) != 2:
        print("Invalid number of arguments")
        print(USAGE)
        return 1
    dateStr = sys.argv[1]
    
    # Validate input date
    match = re.match("(\d{4})-(\d\d)", dateStr)
    if match == None:
        print("Input date " + dateStr + " does not match required pattern")
        print(USAGE)
        return 1
    
    # Extract the year and month from the input date
    year = match.group(1)
    month = match.group(2)
    
    # Change date to previous month
    if month == "01":
        month = "12"
        year = int(year)
        year = str(year - 1)
    else:
        month = int(month)
        month = "%02d" % (month - 1)
    
    # Change report names to use previous month
    files = [f for f in os.listdir('.') if os.path.isfile(f)]
    print("Found " + str(len(files)) + " files to possibly rename in " + os.path.abspath('.'))
    for f in files:
        oldFileName = f
        if dateStr in oldFileName:
            newFileName = oldFileName.replace(dateStr, year + "-" + month)
            os.rename(oldFileName, newFileName)
        else:
            print(dateStr + " not found in " + oldFileName)

if __name__ == "__main__":
    sys.exit(main())
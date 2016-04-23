#!/bin/bash

#http://www.nyc.gov/html/tlc/html/about/trip_record_data.shtml
for year in `seq 2009 2015`;
do
    echo $year
    if [ ! -d "$DIRECTORY" ]; then
        mkdir $year
    fi    
        
    for month in `seq 01 12`;
    do
        if [ $month -lt 10 ]; then 
            filename="$year-0$month.csv"
        else
            filename="$year-$month.csv"
        fi
        echo $filename
        
        wget --no-check-certificate "https://storage.googleapis.com/tlc-trip-data/$year/yellow_tripdata_$filename"  -P $year/
        wget --no-check-certificate "https://storage.googleapis.com/tlc-trip-data/$year/green_tripdata_$filename"  -P $year/
        
    done
    
done  
library(ggmap)
library(ggplot2)

#http://stackoverflow.com/questions/23130604/r-plot-coordinates-on-map
#http://gis.stackexchange.com/questions/42224/how-to-create-a-polyline-based-heatmap-from-gps-tracks
plot_taxi_geolife_dir <- function(dirPath) {
  gpsFiles <- list.files(dirPath, full.names = TRUE, pattern = "*.txt")
  lonCenter <- 0
  latCenter <- 0
  
  dataFrame <- structure(
    list(
      taxiId = c(),
      dateTime = c(),
      longitude = c(),
      latitude = c()      
    ) ,.Names = c("taxiId", "date", "longitude", "latitude"), class = "data.frame"
  )  
  
  
  for ( filename in gpsFiles ) {
    print(filename)
    gpsData <- try(read.csv(filename))
    if (!inherits(gpsData, 'try-error')){
      gpsData <- setNames(gpsData, c("taxiId", "date", "longitude", "latitude"))
      lonCenter <- lonCenter + mean(gpsData[[3]], na.rm = TRUE)
      latCenter <- latCenter + mean(gpsData[[4]], na.rm = TRUE)
      
      dataFrame <- rbind(dataFrame, gpsData)
      #print( paste(length(dataFrame[[1]]), " (+", length(gpsData[[1]]), ")") )
      
    }

  }
  
  lonCenter <- lonCenter / length(gpsFiles)
  latCenter <- latCenter / length(gpsFiles)
  map <- get_map(location = c(lon = lonCenter, lat = latCenter), zoom = 8, maptype = "satellite")
  
  mapImage <- ggmap(map) + 
    geom_point(
      data = dataFrame, 
      aes(x = longitude, y = latitude, fill = taxiId),
      size = 3, shape = 21
    ) + 
    guides(
      fill=FALSE, alpha=FALSE, size=FALSE, colour = FALSE
    )
  
  ggsave(mapImage, filename = "china2.png")

}


#taxi id, date time, longitude, latitude
#1,2008-02-02 15:36:08,116.51172,39.92123
plot_taxi_geolife <- function(filePath) {
  gpsData <- read.csv(filePath)
  lonCenter <- mean(gpsData[[3]], na.rm = TRUE)
  latCenter <- mean(gpsData[[4]], na.rm = TRUE)
  
  # Get a map
  map <- get_map(location = c(lon = lonCenter, lat = latCenter), zoom = 10)
  
  dataFrame <- structure(
    list(
      taxiId = gpsData[[1]],
      longitude = gpsData[[3]],
      latitude = gpsData[[4]]      
     ),
    .Names = c("id", "longitude", "latitude"), class = "data.frame"
  )
  
  ggmap(map) + 
    geom_point(
       data = dataFrame, 
       aes(x = longitude, y = latitude, fill = "red", colour = "red", alpha = 1/3),
       size = 3, shape = 21
    ) + 
    guides(
      fill=FALSE, alpha=FALSE, size=FALSE, colour = FALSE
    )
}



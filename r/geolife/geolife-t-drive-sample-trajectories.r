library(ggmap)
library(ggplot2)


#
#taxi id, date time, longitude, latitude
#1,2008-02-02 15:36:08,116.51172,39.92123
plot_segment_taxi_geolife <- function(filePath) {
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
    geom_path(
      data = dataFrame, 
      aes(x = longitude, y = latitude, fill = "red", colour = "red", alpha = 1/3),
      size = 1, shape = 21
    ) + 
    guides(
      fill=FALSE, alpha=FALSE, size=FALSE, colour = FALSE
    )
}






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



#1,2008-02-02 15:36:08,116.51172,39.92123
plot_plt_geolife <- function(filePath, zoom = 4) {
  #gpsData <- read.csv(filePath)
  pltFile <- readLines(filePath)
  pltData <- pltFile[7:length(pltFile)]
  
  gpsData <- read.csv(textConnection(pltData))
  
  
  lonCenter <- mean(gpsData[[2]], na.rm = TRUE)
  latCenter <- mean(gpsData[[1]], na.rm = TRUE)
  
  # Get a map
  map <- get_map(location = c(lon = lonCenter, lat = latCenter), zoom)
  
  dataFrame <- structure(
    list(
      longitude = gpsData[[2]],
      latitude = gpsData[[1]]      
    ),
    .Names = c("longitude", "latitude"), class = "data.frame"
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


plot_csv_processed <- function(filePath, zoom = 10) {
  gpsData <- read.csv(filePath)
  lonCenter <- mean(gpsData[[2]], na.rm = TRUE)
  latCenter <- mean(gpsData[[3]], na.rm = TRUE)
  
  # Get a map
  map <- get_map(location = c(lon = lonCenter, lat = latCenter), zoom)
  
  dataFrame <- structure(
    list(
      longitude = gpsData[[2]],
      latitude = gpsData[[3]]      
    ),
    .Names = c("longitude", "latitude"), class = "data.frame"
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


plot_csv_data_noise <- function(dataFilePath, noiseFilePath ,zoom = 10, latCenter = 0, lonCenter = 0) {
  #if (file.info(noiseFilePath)$size == 0) { 
  #  plot_csv_processed(dataFilePath, zoom)  
  #}
  
  gpsData <- read.csv(dataFilePath)
  gpsNoise <- read.csv(noiseFilePath)
  
  if (lonCenter  == 0) {
    lonCenter <- mean(gpsData[[2]], na.rm = TRUE)  
  }
  
  if(latCenter == 0) {
    latCenter <- mean(gpsData[[3]], na.rm = TRUE)
  }
  
  
  # Get a map
  map <- get_map(location = c(lon = lonCenter, lat = latCenter), zoom)
  
  dataFrame <- structure(
    list(
      longitude = gpsData[[2]],
      latitude = gpsData[[3]]      
    ),
    .Names = c("longitude", "latitude"), class = "data.frame"
  )
  
  noiseFrame <- structure(
    list(
      longitude = gpsNoise[[2]],
      latitude = gpsNoise[[3]]      
    ),
    .Names = c("longitude", "latitude"), class = "data.frame"
  )
  
  ggmap(map) + 
    geom_point(
      data = dataFrame, 
      aes(x = longitude, y = latitude, colour = "trajectory", alpha = 1/3),
      size = 3, shape = 21
    ) + 
    geom_point(
      data = noiseFrame, 
      aes(x = longitude, y = latitude, colour = "noise", alpha = 1/3),
      size = 4, shape = 17
    ) +
    guides(
      fill=FALSE, alpha=FALSE, size=FALSE, colour = FALSE
    ) + 
    scale_colour_manual(name="",  
                        values = c("trajectory"="red", "noise"="black"))
}


plot_ester_heuristic <- function() {
  kdistance <- c(79.0,76.0,68.0,68.0,67.0,59.0,59.0,59.0,58.0,57.0,55.0,54.0,54.0,54.0,52.0,51.0,51.0,50.0,50.0,49.0,49.0,49.0,48.0,48.0,48.0,47.0,46.0,46.0,45.0,45.0,45.0,44.0,44.0,44.0,43.0,43.0,43.0,43.0,42.0,42.0,42.0,42.0,42.0,42.0,42.0,42.0,42.0,41.0,41.0,41.0,41.0,41.0,41.0,41.0,41.0,41.0,41.0,41.0,41.0,41.0,41.0,41.0,41.0,40.0,40.0,40.0,40.0,40.0,40.0,40.0,40.0,40.0,40.0,40.0,40.0,40.0,40.0,40.0,40.0,40.0,40.0,40.0,40.0,40.0,40.0,40.0,40.0,40.0,40.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,39.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,38.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,37.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,36.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,35.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,34.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,33.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,32.0,31.0,31.0,31.0,31.0,31.0,31.0,31.0,31.0,31.0,31.0,31.0,31.0,31.0,31.0,31.0,31.0,31.0,31.0,30.0,30.0,30.0,29.0,29.0,29.0,29.0,28.0,28.0,28.0,27.0,27.0,26.0,26.0,25.0,25.0,25.0,24.0,23.0,23.0,23.0,23.0,23.0,22.0,22.0,21.0,21.0,19.0,19.0,19.0,19.0,18.0,18.0,18.0,17.0,17.0,17.0,16.0,16.0,16.0,15.0,15.0,15.0,15.0,15.0,14.0,14.0,14.0,14.0,14.0,12.0,12.0,11.0,11.0,11.0,11.0,11.0,11.0,11.0,11.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0,9.0,9.0,9.0,9.0,9.0,9.0,8.0,7.0,7.0,7.0,7.0,7.0,7.0,7.0,6.0,6.0,6.0,6.0,5.0,5.0,5.0,4.0,4.0,4.0,4.0,3.0,3.0,3.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)
  plot(kdistance, axes=FALSE)
  axis(side=2, at=seq(0,80, by=1))
  axis(side=1, at=seq(0,800, by=100))
  box()
}

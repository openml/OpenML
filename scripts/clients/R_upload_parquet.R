#!/usr/bin/env Rscript
# Simple R example: convert a data.frame (or CSV) to parquet and upload
# Requires: install.packages(c('arrow','httr'))

library(arrow)
library(httr)

args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 4) {
  cat("Usage: Rscript R_upload_parquet.R <api_url> <api_key> <file> <name> [description]\n")
  quit(status = 1)
}

api_url <- args[1]
api_key <- args[2]
file_in <- args[3]
name <- args[4]
description <- ifelse(length(args) >= 5, args[5], 'Uploaded via R_upload_parquet.R')

parquet_path <- file_in
if (tolower(tools::file_ext(file_in)) == 'csv') {
  df <- read.csv(file_in, stringsAsFactors = FALSE)
  parquet_path <- sub('\\.[Cc][Ss][Vv]$', '.parquet', file_in)
  write_parquet(df, parquet_path)
}

build_description_xml <- function(name, description, fmt='parquet', visibility='public') {
  sprintf('<?xml version="1.0" encoding="UTF-8"?>\n<oml:data xmlns:oml="http://openml.org/openml">\n  <oml:name>%s</oml:name>\n  <oml:description>%s</oml:description>\n  <oml:version>1</oml:version>\n  <oml:format>%s</oml:format>\n  <oml:visibility>%s</oml:visibility>\n</oml:data>', name, description, fmt, visibility)
}

desc_xml <- build_description_xml(name, description, 'parquet')

res <- POST(
  url = api_url,
  body = list(
    api_key = api_key,
    description = upload_file(path = NULL, type = 'application/xml', file = charToRaw(desc_xml)),
    dataset = upload_file(parquet_path, type = 'application/octet-stream')
  ),
  encode = "multipart"
)

if (http_error(res)) {
  cat('Upload failed:', content(res, 'text', encoding='UTF-8'), '\n')
  quit(status = 1)
} else {
  cat('Upload response:\n')
  print(content(res, 'text', encoding='UTF-8'))
}

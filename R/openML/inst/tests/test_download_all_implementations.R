# FIXME do this on CRAN?
context("download all implementations")

test_that("download all implementations", {
  # FIXME extend test to all implementations on openml server
  # get them from sql, and remove rdata in test dir

  #impls = load2("impl_ids_for_test.RData")
  #FIXME remove or define complete test as external or whatever
  errs = c()
  for (i in seq_along(impls)) {
    id = impls[i]
    print(id)
    res = try({
      impl = downloadOpenMLImplementation(id=id, show.info=TRUE)
    })
    if (is.error(res)) {
      errs = c(errs, id)
    }
  }
  #FIXME remove
  xxx <<- errs
  print("Errors:")
  print(errs)
})  
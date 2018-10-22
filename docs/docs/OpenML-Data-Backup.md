As OpenML currently consists of a vast amount of valuable data, therefore it is important to have back-up policies for the data in place.

Currently, we have the following important data that requires to be back-upped:
- The databases (stored on the SSD)
    - openml, for userdata
    - openml_expdb, for experimental data
- The Experiment data (stored on the HDD)
    - In the data folder

The following security strategies are in place:
- The server disks are setup as RAID-arrays
    - for the SSD: RAID 1
    - for the HDD: RAID 10
- The database is backupped every (X) days and stored on the TU/e testserver. 
- The data should be rsynced every day to the TU/e testserver. (Currently not applicable due to space constraints, working on a solution. Last backup: begin of March, still in place) 

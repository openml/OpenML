ALTER TABLE `task_math_function` CHANGE `math_function_id` `math_function_name` VARCHAR( 64 ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL ;
ALTER TABLE `fold`
  DROP PRIMARY KEY,
   ADD PRIMARY KEY(
     `task_id`,
     `fold_id`,
     `repeat_id`,
     `set`);
	 
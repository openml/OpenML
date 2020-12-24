INSERT INTO `users` (`id`, `username`, `password`, `email`, `active`,`session_hash`, `gamification_visibility`) VALUES
(1, 'admin@openml.org', MD5('admin'), 'admin@openml.org', 1,'0123456789abcdef0123456789abcdef', 'show'),
(2, 'user@openml.org', MD5('user'), 'user@openml.org', 1, '00112233445566778899aabbccddeeff', 'show');

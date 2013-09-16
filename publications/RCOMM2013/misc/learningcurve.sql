SELECT ps.value as gamma, e.value as accuracy 
FROM cvrun r, algorithm_setup s, function_setup kernel, dataset d, input_setting ps, evaluation e 
WHERE r.learner=s.sid and s.algorithm='SVM' AND kernel.parent=s.sid AND kernel.function='RBFKernel' AND ps.setup=s.sid AND ps.input='weka.SMO(1.53.2.2)_G' AND e.source=r.rid AND e.function='predictive_accuracy' AND r.inputdata=d.did AND d.name='letter'

import numpy as np
from sklearn import preprocessing
from sklearn.neural_network import MLPClassifier
from sklearn.neighbors import KNeighborsClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn import tree
from time import time
from sklearn.utils import shuffle
from sklearn.metrics import accuracy_score, precision_recall_fscore_support
import random
import pdb

fw=open("clf_results.txt", "a+")
#Get train/test file and create an np_array from it
train_fname = "train_samples.csv"
test_fname = "test_samples.csv"
X_train = np.loadtxt(train_fname, delimiter=",", usecols = (1,2,3,4,5,6,7,8,9,10,11))
Y_train = np.loadtxt(train_fname, dtype='int', delimiter=",", usecols = (12))

X_test = np.loadtxt(test_fname, delimiter=",", usecols = (1,2,3,4,5,6,7,8,9,10,11))
Y_test = np.loadtxt(test_fname, dtype='int', delimiter=",", usecols = (12))


#scale X_train using MinMaxScaler in range (0,1), transform X_test too
min_max_scaler = preprocessing.MinMaxScaler()
X_train = min_max_scaler.fit_transform(X_train)
X_test = min_max_scaler.transform(X_test)

#get LoopID as a list
LoopID_list = np.loadtxt(test_fname, dtype='int', delimiter=",", usecols = (0))


#shuffle dataset, including loopID
X_train, Y_train = shuffle(X_train, Y_train, random_state=0)
X_test, Y_test, LoopID_list = shuffle(X_test, Y_test, LoopID_list, random_state=0)


#list of all_labels - used to record per unroll_count prec_recall metrics
unroll_count_list = [1,2,3,4,5,8,10,16]



# #############################################################################
# Benchmark classifiers
def benchmark(clf, name):
    print('_' * 80)
    print("Training: ")
    print(clf)
    t0 = time()
    clf.fit(X_train, Y_train)
    train_time = time() - t0
    train_score = clf.score(X_train, Y_train)
    print("Training set score: %f" % train_score)
    print("train time: %0.3fs" % train_time)   
    t0 = time()
    test_score = clf.score(X_test, Y_test)
    print("Test set score: %f" % test_score)
    test_time = time() - t0
    print("test time:  %0.3fs" % test_time)

    pred = clf.predict(X_test)



    fw1=open(name+".txt", "a+")
    #Get per label prec_recall_fscore_support as well as overall values, accuracy
    prec_recall = precision_recall_fscore_support(Y_test, pred)
    fw.write("Classifier: "+name+", Accuracy: "+str(test_score)+'\n')
    for idx, unroll_count in enumerate(unroll_count_list):
      fw.write("Unroll Factor: "+str(unroll_count)+", Precision: "+str(prec_recall[0][idx])+\
        ", Recall: "+str(prec_recall[1][idx])+", F-1 Score: "+str(prec_recall[2][idx])+'\n')
    prec_recall = precision_recall_fscore_support(Y_test, pred, average = 'weighted')
    fw.write("Overall Weighted: - Precision: "+str(prec_recall[0])+\
        ", Recall: "+str(prec_recall[1])+", F-1 Score: "+str(prec_recall[2])+'\n\n')
    fw1.write("LoopID,GroundTruth,Prediction"+'\n')
    for idx, result in enumerate(pred):
      fw1.write(str(LoopID_list[idx])+","+str(Y_test[idx])+","+str(pred[idx])+'\n')
    fw1.close()    
    
    
      
    
    

results = []

for clf, name in (
        (KNeighborsClassifier(n_neighbors=10), "kNN"),
        (RandomForestClassifier(n_estimators=100), "Random-forest"),
        (MLPClassifier(hidden_layer_sizes=(100,), max_iter=500,solver='lbfgs'), "MLPClassifier"),
        (tree.DecisionTreeClassifier(), "DecisionTree")
      ):
    print('=' * 80)
    print(name)
    print()
    results.append(benchmark(clf,name))


#Generate baseline outputs
#random
secure_random = random.SystemRandom()
#choose n random choices from list where n is length of test_items
random_answers = [secure_random.choice(unroll_count_list) for sample in Y_test]
prec_recall = precision_recall_fscore_support(Y_test, random_answers)
fw.write("Classifier: Random Label"+", Accuracy: "+str(accuracy_score(Y_test, random_answers))+'\n')
for idx, unroll_count in enumerate(unroll_count_list):
  fw.write("Unroll Factor: "+str(unroll_count)+", Precision: "+str(prec_recall[0][idx])+\
    ", Recall: "+str(prec_recall[1][idx])+", F-1 Score: "+str(prec_recall[2][idx])+'\n')
prec_recall = precision_recall_fscore_support(Y_test, random_answers, average = 'weighted')
fw.write("Overall Weighted: - Precision: "+str(prec_recall[0])+\
    ", Recall: "+str(prec_recall[1])+", F-1 Score: "+str(prec_recall[2])+'\n\n')
fw1=open("random.txt", "a+")
fw1.write("LoopID,GroundTruth,Prediction"+'\n')
for idx, result in enumerate(Y_test):
  fw1.write(str(LoopID_list[idx])+","+str(Y_test[idx])+","+str(random_answers[idx])+'\n')
fw1.close() 

#most frequently occuring label in dataset
most_freq_label = max(set(list(Y_test)), key=list(Y_test).count)
most_freq_answers = [most_freq_label for sample in Y_test]
prec_recall = precision_recall_fscore_support(Y_test, most_freq_answers)
fw.write("Classifier: Most Frequent Label"+", Accuracy: "+str(accuracy_score(Y_test, most_freq_answers))+'\n')
for idx, unroll_count in enumerate(unroll_count_list):
  fw.write("Unroll Factor: "+str(unroll_count)+", Precision: "+str(prec_recall[0][idx])+\
    ", Recall: "+str(prec_recall[1][idx])+", F-1 Score: "+str(prec_recall[2][idx])+'\n')
prec_recall = precision_recall_fscore_support(Y_test, most_freq_answers, average = 'weighted')
fw.write("Overall Weighted: - Precision: "+str(prec_recall[0])+\
    ", Recall: "+str(prec_recall[1])+", F-1 Score: "+str(prec_recall[2])+'\n\n')
fw1=open("most_freq.txt", "a+")
fw1.write("LoopID,GroundTruth,Prediction"+'\n')
for idx, result in enumerate(Y_test):
  fw1.write(str(LoopID_list[idx])+","+str(Y_test[idx])+","+str(most_freq_answers[idx])+'\n')
fw1.close()


fw.close()
#pdb.set_trace()


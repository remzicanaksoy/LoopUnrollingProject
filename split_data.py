import numpy as np
import pdb
'''
How to divide data into test / train

Load every item in merged data into dict represented by unroll_factor
Each dict_value  is list of ids matched with all features

Randomly permute_list range and make those below test_cutoff as train and those above as test
'''


#initialize test_portion of dataset
test_portion = 0.1


def aggregate_similar_unrolls():
  with open("mergedData.txt") as fin:
    dict_of_label_lists = {}
    for line in fin:
      line_elements = line.rstrip().split(",")
      if line_elements[12] not in dict_of_label_lists:
        dict_of_label_lists[line_elements[12]] = []
        dict_of_label_lists[line_elements[12]].append(line.rstrip())
      else: 
        dict_of_label_lists[line_elements[12]].append(line.rstrip())
  return dict_of_label_lists



#get a dict of unroll_factors, each value if dict is a list of all loops with that factor
dict_label_lists = aggregate_similar_unrolls() 
unroll_split_idx_dict = {} #holds permutations of each list of loops with common unroll_factor
unroll_num_train_dict = {} #holds value of train_count per unroll_factor. Matches distribution of dataset
train_list = [] #list of all items to train
test_list = [] #list of all items to test  
for key in dict_label_lists:
  #pdb.set_trace()
  key_ctr = 0
  #Get permuted order for each unroll_factor_list
  unroll_split_idx_dict[key] = np.random.permutation(len(dict_label_lists[key]))
  #Get the number of train samples for each unroll_factor based on total_count in dataset
  unroll_num_train_dict[key] = int(np.round(len(dict_label_lists[key]) * (1. - test_portion)))
  with open("mergedData.txt") as dataFile:
    for line in dataFile:
      line_elements = line.rstrip().split(",")
      if line_elements[12]  == key:
        #if the idx of the sample in unroll_split_idx_dict is less than num_train, it is in train set
        if np.where(unroll_split_idx_dict[key] == key_ctr)[0] < unroll_num_train_dict[key]:
          train_list.append(line)
        #else add it to test_set
        else:
          test_list.append(line)
        key_ctr+=1
#pdb.set_trace()
print("Train length", len(train_list))
print("Test length", len(test_list))
fw=open("train_samples.csv", 'w')
#json.dump(train_list, fw)
fw.write(''.join(train_list))
fw.close()

fw=open("test_samples.csv", 'w')
fw.write(''.join(test_list))
fw.close()


  

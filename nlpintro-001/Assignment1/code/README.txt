1. performance using bad features, takes around 5s to run. on Mac 2.3Ghz
The badfeature.model is relatively fast to run. compared to the good features.

to explore all the features is definitely impossible.
so SVM learning is used here to guide the next transition based on the learnings.
while SVM is advanced method for classification, its performance is coupled with features used as well.

adding tag/ctag to the feature space immediately improved the precision greatly.
and the context of the sentence during parsing provides very useful information but at relatively high performance cost.

there was a very interesting mistake I made during refactoring the append_feature method.
 - I passed the word index instead of the stack/buffer position based index to the feature
 - this immediately worsened performance from 0.7 to 0.2
 - seems word position alone is not good feature.
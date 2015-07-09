il = 2;              % input layer
hl = 2;              % hidden layer
nl = 4;              % number of labels
nn = [ 1:18 ] / 10;  % nn_params
X = cos([1 2 ; 3 4 ; 5 6]);
y = [4; 2; 3];
lambda = 4;
[J grad] = nnCostFunction(nn, il, hl, nl, X, y, lambda)

%https://www.coursera.org/learn/machine-learning/module/Aah2H/discussions/a8Kce_WxEeS16yIACyoj1Q/replies/KrpdjiTCEeWYoCIAC5MBcw

a=1
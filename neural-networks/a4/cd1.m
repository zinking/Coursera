function ret = cd1(rbm_w, visible_data)
% <rbm_w> is a matrix of size H * V 
% <number of hidden units> by <number of visible units>
% <visible_data> is a (possibly but not necessarily binary) matrix of V * C size
% <number of visible units> by <number of data cases>
% The returned value is the gradient approximation produced by CD-1. It's of the same shape as <rbm_w>.
    C = size(visible_data,2);
    visible_data = sample_bernoulli(visible_data); %question 9
    HiddenHC = visible_state_to_hidden_probabilities(rbm_w, visible_data);
    SHiddenHC = sample_bernoulli(HiddenHC);
    pdg = configuration_goodness_gradient(visible_data, SHiddenHC);
    %nrbm_w = rbm_w - pdg;
    
    newVisVC = hidden_state_to_visible_probabilities(rbm_w, SHiddenHC);
    SNewVisVC = sample_bernoulli(newVisVC);
    
    NHiddenHC = visible_state_to_hidden_probabilities(rbm_w, SNewVisVC);
    %SNHiddenHC = sample_bernoulli(NHiddenHC); %question 8
    SNHiddenHC = NHiddenHC;
    ndg = configuration_goodness_gradient(SNewVisVC, SNHiddenHC);
    nnrbm_w =  (pdg - ndg);
    ret = nnrbm_w;
    
end

function G = configuration_goodness(rbm_w, visible_state, hidden_state)
% <rbm_w> is a matrix of size <number of hidden units> by <number of
% visible units> H * V
% <visible_state> is a binary matrix of size V*C V*1
% <number of visible units> by <number of configurations that we're handling in parallel>.
% <hidden_state> is a binary matrix of size H*C H*1
% <number of hidden units> by <number of configurations that we're handling in parallel>.
% This returns a scalar: the mean over cases of the goodness (negative energy) of the described configurations.
% 1*H*H*V*V*1 = 1*1

    GG = hidden_state'*rbm_w*visible_state;
    G = mean(mean(GG));
end

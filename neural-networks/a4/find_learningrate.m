function strs = find_learningrate()
    strs = cell(1,220);
    counter = 1;
    expect = 0.322890;
    for lr = 0:0.005:1
        err = a4_main(300, .02, lr, 1000);
        r = sprintf('learning rate %f error %f', lr, err);
        if err >= expect/2 && err < 2*expect
            fprintf('********************');
            fprintf(r);
        end;
        strs{counter} = r;
        counter = counter +1;
    end
    strs = strs'
    
function strs = a8()
    wds = [0, 1, 0.1, 0.0001, 0.001];
    strs = cell(1,14);
    counter = 1;
    for wd = 1:5
        w = wds(wd);
        results = a3(w, 10, 70, 0.5, 0.9, false, 4);
        r = sprintf('wd %f,validation loss %f, validation classification error %f', w, results(2,1), results(2,2));
        strs{counter} = r;
        counter = counter+1;
    end
end
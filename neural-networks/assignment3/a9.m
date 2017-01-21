function strs = a9()
    hds = [100,30,10,200,130];
    strs = cell(1,14);
    counter = 1;
    for wd = 1:5
        h = hds(wd);
        results = a3(0, h, 70, 0.5, 0.9, false, 4);
        r = sprintf('hd %f,validation loss %f, validation classification error %f', h, results(2,1), results(2,2));
        strs{counter} = r;
        counter = counter+1;
    end
end
function strs = a4()
    lrs = [0.002, 0.01, 0.05, 0.2, 1.0, 5.0, 20.];
    mos = [0, 0.9];
    strs = cell(1,14);
    counter = 1;
    for mo = 1:2,
        for lr = 1:7,
            results = a3(0, 10, 70, lrs(lr), mos(mo), false, 4);
            r = sprintf('lr %f mo %f, test loss %f, test classification error %f', lrs(lr), mos(mo), results(3,1), results(3,2));
            strs{counter} = r;
            counter = counter +1;
        end
    end
end
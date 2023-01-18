use std::ops::AddAssign;

pub fn count_sort(arr: &mut [usize]) {
    let max = arr.iter().max().unwrap();
    let mut occurs: Vec<usize> = vec![0; max + 1];

    for px in arr.iter() {
        let x = *px;
        occurs[x] += 1;
    }

    let mut i = 0;
    for (data, &n) in occurs.iter().enumerate() {
        for _ in 0..n {
            arr[i] = data;
            i += 1;
        }
    }
}

pub fn gcount_sort<T: Into<usize> + From<usize> + AddAssign + Copy + Ord>(arr: &mut[T]) {
    let pmax = arr.iter().max().unwrap();
    let max = (*pmax).into() as usize;
    let mut occurs: Vec<usize> = vec![0; max + 1];
    for pdata in arr.iter() {
        let data = *pdata;
        occurs[data.into() as usize] += 1;
    }
    let mut i = 0;

    for (data, &n) in occurs.iter().enumerate() {
        for _ in 0..n {
            arr[i] = T::from(data);
            i += 1;
        }
    }
}

#[cfg(test)]
mod test {
    use crate::sort::count_sort::{count_sort, gcount_sort};

    #[test]
    fn count_sort_descend() {
        let mut arr = vec![6, 5, 4, 3, 2, 1];
        count_sort(&mut arr);
        assert_eq!(arr, vec![1, 2, 3, 4, 5, 6]);
    }

    #[test]
    fn gcount_sort_usize() {
        let mut arr : Vec<usize> = vec![6, 5, 4, 3, 2, 1];
        gcount_sort(&mut arr);
        assert_eq!(arr, vec![1, 2, 3, 4, 5, 6]);
    }

    #[test]
    fn gcount_sort_u8() {
        let mut arr : Vec<u8> = vec![6, 5, 4, 3, 2, 1];
        // TODO: fix me
        // gcount_sort(&mut arr);
        // assert_eq!(arr, vec![1, 2, 3, 4, 5, 6]);
    }
}
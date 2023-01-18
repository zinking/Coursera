
pub fn merge_sort<T>(arr: &mut [T])
    where T: PartialOrd + Clone + Default {
    let len = arr.len();
    if len < 2 {
        return;
    }
    merge_sort_range(arr, 0, len - 1);
}

pub fn merge_sort_range<T>(arr: &mut [T], l: usize, r: usize)
    where T: PartialOrd + Clone + Default {
    // println!("merge l {}, r {}", l, r);
    if l < r {
        let mid = l + ((r - l) >> 1);
        merge_sort_range(arr, l, mid);
        merge_sort_range(arr, mid + 1, r);
        merge(arr, l, mid, r);
    }

}

pub fn merge<T>(arr: &mut [T], l: usize, m: usize, r: usize)
    where T: PartialOrd + Clone + Default {
    // println!("merge l {}, m {}, r {}", l, m, r);
    let mut arr1 = arr[l..=m].to_vec();
    let mut arr2 = arr[m + 1..=r].to_vec();
    let mut i1 = 0;
    let mut i2 = 0;
    while i1 < arr1.len() && i2 < arr2.len() {
        if arr1[i1] < arr2[i2] {
            arr[l + i1 + i2] = std::mem::take(&mut arr1[i1]);
            i1 += 1;
        } else {
            arr[l + i1 + i2] = std::mem::take(&mut arr2[i2]);
            i2 += 1;
        }
    }

    while i1 < arr1.len() {
        arr[l + i1 + i2] = std::mem::take(&mut arr1[i1]);
        i1 += 1;
    }

    while i2 < arr2.len() {
        arr[l + i1 + i2] = std::mem::take(&mut arr2[i2]);
        i2 += 1;
    }
}

#[cfg(test)]
mod test {
    use crate::sort::merge_sort::merge_sort;

    #[test]
    fn test_empty_vec() {
        let mut arr: Vec<u8> = vec![];
        merge_sort(&mut arr);
        assert_eq!(arr, vec![])
    }

    #[test]
    fn test_number_vec() {
        let mut arr = vec![3, 1, 5, 7];
        merge_sort(&mut arr);
        assert_eq!(arr, vec![1, 3, 5, 7])
    }

    #[test]
    fn test_number_vec2() {
        let mut arr = vec![7, 49, 73, 58, 30, 72, 44, 78, 23, 9];
        merge_sort(&mut arr);
        assert_eq!(arr, vec![7, 9, 23, 30, 44, 49, 58, 72, 73, 78]);
    }

}
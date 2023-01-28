
pub fn quick_select<T: Ord>(arr: &mut [T], k: usize) -> usize {
    let len = arr.len();
    if len <= k {
        panic!("array length less than k");
    }

    let mut l = 0;
    let mut r = len - 1;
    let mut kk = k;
    while kk > 0 {
        let p = partition(arr, l, r);
        if p == l + kk {
            return p;
        } else if p > l + kk {
            r = p - 1;
        } else {
            l = p;
            kk -= p + 1;
        }
    }

    return 0;
}

pub fn partition<T: Ord>(arr: &mut [T], l: usize, r: usize) -> usize {
    let mut j = l;
    let pivot = r;
    for i in l..r {
        if arr[i] < arr[pivot] {
            j += 1;
            // arr.swap(i, j);
        }
    }
    // arr.swap(j + 1, pivot);
    j
}

#[cfg(test)]
mod tests {
    use crate::search::quick_select::quick_select;

    #[test]
    fn test_quick_select() {
        let mut arr = vec![1,3,4,2];
        let r = quick_select(&mut arr, 2);
        assert_eq!(r, 3)
    }
}
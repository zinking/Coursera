use crate::sort::bubble_sort::bubble_sort;

pub fn bucket_sort(arr: &[usize]) -> Vec<usize> {
    if arr.is_empty() {
        return vec![]
    }

    let max = *arr.iter().max().unwrap();
    let len = arr.len();
    let mut buckets : Vec<Vec<usize>> = vec![vec![]; len + 1];

    for px in arr {
        let x = *px;
        buckets[len * x / max].push(x);
    }

    for bucket in buckets.iter_mut() {
        bubble_sort(bucket)
    }

    let mut result = vec![];
    for bucket in buckets {
        for x in bucket {
            result.push(x);
        }
    }

    result
}



#[cfg(test)]
mod tests {
    use crate::sort::bubble_sort::is_sorted;
    use crate::sort::bucket_sort::bucket_sort;

    #[test]
    fn empty() {
        let empty: [usize; 0] = [];
        let rr = bucket_sort(&empty);
        assert!(is_sorted(&rr));
    }

    #[test]
    fn one_element() {
        let arr: [usize; 1] = [3];
        let rr = bucket_sort(&arr);
        assert!(is_sorted(&rr));
    }

    #[test]
    fn two_element() {
        let arr: [usize; 2] = [4, 3];
        let rr = bucket_sort(&arr);
        assert!(is_sorted(&rr));
    }

    #[test]
    fn already_sorted() {
        let arr: [usize; 5] = [1, 3, 5, 7, 9];
        let rr = bucket_sort(&arr);
        assert!(is_sorted(&rr));
    }

    #[test]
    fn basic() {
        let arr: [usize; 4] = [35, 53, 1, 1024];
        let rr = bucket_sort(&arr);
        assert!(is_sorted(&rr));
    }

    #[test]
    fn repeated() {
        let arr: [usize; 5] = [1023, 1023, 1023, 1023, 1023];
        let rr = bucket_sort(&arr);
        assert!(is_sorted(&rr));
    }
}
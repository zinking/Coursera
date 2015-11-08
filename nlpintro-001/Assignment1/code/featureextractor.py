from nltk.compat import python_2_unicode_compatible

printed = False

@python_2_unicode_compatible
class FeatureExtractor(object):
    @staticmethod
    def _check_informative(feat, underscore_is_informative=False):
        """
        Check whether a feature is informative
        """

        if feat is None:
            return False

        if feat == '':
            return False

        if not underscore_is_informative and feat == '_':
            return False

        return True

    @staticmethod
    def find_left_right_dependencies(idx, arcs):
        left_most = 1000000
        right_most = -1
        dep_left_most = ''
        dep_right_most = ''
        for (wi, r, wj) in arcs:
            if wi == idx:
                if (wj > wi) and (wj > right_most):
                    right_most = wj
                    dep_right_most = r
                if (wj < wi) and (wj < left_most):
                    left_most = wj
                    dep_left_most = r
        return dep_left_most, dep_right_most

    @staticmethod
    def find_left_right_neighbor_dependencies(idx, arcs):
        left_near = 1000000
        right_near = -1
        dep_left_near = ''
        dep_right_near = ''
        for (wi, r, wj) in arcs:
            if wi == idx:
                if (wj > wi) and (wj < right_near):
                    right_near = wj
                    dep_right_near = r
                if (wj < wi) and (wj > left_near):
                    left_near = wj
                    dep_left_near = r
        return dep_left_near, dep_right_near

    @staticmethod
    def extract_features(tokens, buffer, stack, arcs):
        """
        This function returns a list of string features for the classifier

        :param tokens: nodes in the dependency graph
        :param stack: partially processed words
        :param buffer: remaining input words
        :param arcs: partially built dependency tree

        :return: list(str)
        """

        """
        Think of some of your own features here! Some standard features are
        described in Table 3.2 on page 31 of Dependency Parsing by Kubler,
        McDonald, and Nivre

        [http://books.google.com/books/about/Dependency_Parsing.html?id=k3iiup7HB9UC]
        """

        result = []


        global printed
        if not printed:
            print("This is not a very good feature extractor!")
            printed = True

        def append_base_feautre(tag,index,token,result):
            if FeatureExtractor._check_informative(token['word'], True):
                result.append('%s_%d_FORM_%s'%(tag,index,token['word']) )

            if FeatureExtractor._check_informative(token['ctag'], True):
                result.append('%s_%d_CTAG_%s'%(tag,index,token['ctag']) )

            if FeatureExtractor._check_informative(token['tag'], True):
                result.append('%s_%d_TAG_%s'%(tag,index,token['tag']) )

            if 'feats' in token and FeatureExtractor._check_informative(token['feats']):
                feats = token['feats'].split("|")
                for feat in feats:
                    result.append('%s_%d_FEATS_%s'%(tag,index,feat) )
        # an example set of features:
        if stack:
            stack_idx0 = stack[-1]
            token = tokens[stack_idx0]
            append_base_feautre('STK',0,token,result)
            if len(stack)>1:
                stack_idx1 = stack[-2]
                token = tokens[stack_idx1]
                append_base_feautre('STK',1,token,result)

            # Left most, right most dependency of stack[0]
            #dep_left_most, dep_right_most = FeatureExtractor.find_left_right_dependencies(stack_idx0, arcs)
            dep_left_most, dep_right_most = FeatureExtractor.find_left_right_neighbor_dependencies(stack_idx0, arcs)

            if FeatureExtractor._check_informative(dep_left_most):
                result.append('STK_0_LDEP_' + dep_left_most)
            if FeatureExtractor._check_informative(dep_right_most):
                result.append('STK_0_RDEP_' + dep_right_most)


        if buffer:
            buffer_idx0 = buffer[0]
            token = tokens[buffer_idx0]
            append_base_feautre('BUF',0,token,result)
            if len(buffer)>1:
                buffer_idx1 = buffer[1]
                token = tokens[buffer_idx1]
                append_base_feautre('BUF',1,token,result)

            #dep_left_most, dep_right_most = FeatureExtractor.find_left_right_dependencies(buffer_idx0, arcs)
            dep_left_most, dep_right_most = FeatureExtractor.find_left_right_neighbor_dependencies(buffer_idx0, arcs)

            if FeatureExtractor._check_informative(dep_left_most):
                result.append('BUF_0_LDEP_' + dep_left_most)
            if FeatureExtractor._check_informative(dep_right_most):
                result.append('BUF_0_RDEP_' + dep_right_most)



        return result
